package mohit.voice.twinmind.gemini

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

object AudioConverter {

    fun convertM4aToWav(input: File, output: File, sampleRate: Int = 16000, onComplete: (Boolean) -> Unit) {
        try {
            val extractor = MediaExtractor()
            extractor.setDataSource(input.absolutePath)

            // Select audio track
            var audioTrackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                if (format.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    extractor.selectTrack(i)
                    break
                }
            }
            if (audioTrackIndex == -1) return onComplete(false)

            val format = extractor.getTrackFormat(audioTrackIndex)
            val mime = format.getString(MediaFormat.KEY_MIME)!!
            val codec = MediaCodec.createDecoderByType(mime)
            codec.configure(format, null, null, 0)
            codec.start()

            val wavOut = FileOutputStream(output)

            // Write temporary WAV header (will be fixed later)
            writeWavHeader(wavOut, sampleRate, 1, 16, 0)

            val bufferInfo = MediaCodec.BufferInfo()
            var totalBytes = 0

            while (true) {
                val inputBufferIndex = codec.dequeueInputBuffer(10000)
                if (inputBufferIndex >= 0) {
                    val buffer = codec.getInputBuffer(inputBufferIndex)!!
                    val sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize < 0) {
                        codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        break
                    }
                    codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.sampleTime, 0)
                    extractor.advance()
                }

                val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 10000)
                if (outputBufferIndex >= 0) {
                    val outBuffer = codec.getOutputBuffer(outputBufferIndex)!!
                    val chunk = ByteArray(bufferInfo.size)
                    outBuffer.get(chunk)
                    wavOut.write(chunk)
                    totalBytes += chunk.size
                    codec.releaseOutputBuffer(outputBufferIndex, false)
                }

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) break
            }

            codec.stop()
            codec.release()
            extractor.release()

            // Fix WAV header size now that we know totalBytes
            fixWavHeader(output, totalBytes, sampleRate, 1, 16)

            wavOut.close()
            onComplete(true)

        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false)
        }
    }

    private fun writeWavHeader(out: FileOutputStream, sampleRate: Int, channels: Int, bitDepth: Int, dataLength: Int) {
        val header = ByteArray(44)
        val byteRate = sampleRate * channels * bitDepth / 8

        java.nio.ByteBuffer.wrap(header).order(java.nio.ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray())
            putInt(dataLength + 36)
            put("WAVE".toByteArray())
            put("fmt ".toByteArray())
            putInt(16)
            putShort(1.toShort())
            putShort(channels.toShort())
            putInt(sampleRate)
            putInt(byteRate)
            putShort((channels * bitDepth / 8).toShort())
            putShort(bitDepth.toShort())
            put("data".toByteArray())
            putInt(dataLength)
        }
        out.write(header)
    }

    private fun fixWavHeader(wavFile: File, pcmDataSize: Int, sampleRate: Int, channels: Int, bitDepth: Int) {
        val raf = java.io.RandomAccessFile(wavFile, "rw")
        val byteRate = sampleRate * channels * bitDepth / 8

        raf.seek(4)
        raf.writeInt(Integer.reverseBytes(36 + pcmDataSize))
        raf.seek(40)
        raf.writeInt(Integer.reverseBytes(pcmDataSize))
        raf.close()
    }



}