# LiveBottomNavigation

> **Use bottomnavigation into build.gradle**

```kotlin
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    } 
}
```


```kotlin
dependencies{
  implementation 'com.github.ir-hesanmanesh:LiveBottomNavigation:1.2.0'
}
```

> **Create Menu**

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
            android:id="@+id/account"
            android:icon="@drawable/ic_home_select"
            android:title="@string/title_account" />

    <item
            android:id="@+id/library"
            android:checked="true"
            android:icon="@drawable/ic_music_note_black_24dp"
            android:title="@string/title_music"/>

    <item
            android:id="@+id/home"
            android:checked="true"
            android:icon="@drawable/ic_home"
            android:title="@string/title_Home"/>

</menu>
```

> **layout add bottomnavigation**

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">

    <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Hello World!"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"/>
    <me.live.bottomnavigation.LiveBottomnavigation
            android:id="@+id/LiveBottomNav"
            android:layout_width="match_parent"
            android:layout_height="74dp"
            android:background="@color/white"
            android:orientation="horizontal"
            app:menu="@menu/live_bottom_nav"
            app:active_color="#4A810C"
            app:passive_color="#6200EA"
            app:pressed_color="#860A0A"
            app:item_text_size="14sp"
            app:item_padding="4dp"
            app:animation_duration="400"
            app:scale_percent="8"
            app:layout_constraintBottom_toBottomOf="parent">

    </me.live.bottomnavigation.LiveBottomnavigation>

</android.support.constraint.ConstraintLayout>
```

> **code Activity**

```kotlin
  LiveBottomNav.setOnNavigationItemChangedListener(object : OnLiveNavigationItemChangeListener {
            override fun onLiveNavigationItemChanged(liveNavigation: LiveBottomnavigation.LiveNavigation) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

```
