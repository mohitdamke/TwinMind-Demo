# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep your service
-keep class mohit.voice.twinmind.RecordService { *; }

# Keep Hilt-injected ViewModels
-keep class mohit.voice.twinmind.viewmodel.NotesProcessViewModel { *; }
-keep class mohit.voice.twinmind.viewmodel.notes.** { *; }

# Keep Room entities & DAOs
-keep class mohit.voice.twinmind.room.entity.** { *; }
-keep class mohit.voice.twinmind.room.dao.** { *; }

# Gemini
-keep class mohit.voice.twinmind.gemini.** { *; }
