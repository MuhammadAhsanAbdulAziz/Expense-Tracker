# -------------------------------
# General Rules
# -------------------------------
-keepclassmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod
-keepnames class kotlin.Metadata
-dontwarn kotlin.**
-dontwarn androidx.**

# -------------------------------
# Coroutines
# -------------------------------
-dontwarn kotlinx.coroutines.**

# -------------------------------
# Room (with KTX)
# -------------------------------
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase_Impl
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# -------------------------------
# Hilt
# -------------------------------
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keepclassmembers class ** {
    @dagger.** <fields>;
    @dagger.** <methods>;
}
-dontwarn dagger.hilt.**
-dontwarn javax.inject.**

# -------------------------------
# Navigation Component
# -------------------------------
-keep class androidx.navigation.NavDirections { *; }
-keep class androidx.navigation.ActionOnlyNavDirections { *; }
# SafeArgs and NavArgs support
-keepclassmembers class ** {
    @androidx.navigation.NavArgs <methods>;
}
# Parcelable support for SafeArgs
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# -------------------------------
# Lottie
# -------------------------------
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# -------------------------------
# MPAndroidChart
# -------------------------------
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# -------------------------------
# DataStore
# -------------------------------
-dontwarn androidx.datastore.**
-keep class androidx.datastore.** { *; }

# -------------------------------
# ViewModel
# -------------------------------
-keep class androidx.lifecycle.ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# -------------------------------
# ExpenseTracker Specific Fixes
# -------------------------------

# Prevent Transaction model from being stripped (used in nav graph or Room)
-keep class com.ahsan.expensetracker.models.Transaction { *; }

# Optional: Keep all models in the package (safe for small projects)
-keep class com.ahsan.expensetracker.models.** { *; }
