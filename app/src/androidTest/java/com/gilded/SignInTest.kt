package com.gilded

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gilded.fragments.SignInFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Test
    fun emptyUsernameError() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SignInFragment())
                .commitNow()
        }

        onView(withId(R.id.email)).check(matches(isDisplayed()))
        onView(withId(R.id.email))
            .perform(typeText("test@gmail.com"))

        onView(withId(R.id.password))
            .perform(typeText("123456"))

        closeSoftKeyboard()

        onView(withId(R.id.signin))
            .perform(click())

        onView(withText("Nom d'usuari invalid"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invalidPasswordError() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SignInFragment())
                .commitNow()
        }

        onView(withId(R.id.username)).check(matches(isDisplayed()))
        onView(withId(R.id.username))
            .perform(typeText("Jan"))

        onView(withId(R.id.email))
            .perform(typeText("2223_jan.lazaro@iticbcn.cat"))

        onView(withId(R.id.password))
            .perform(typeText("123456"))

        closeSoftKeyboard()

        onView(withId(R.id.signin))
            .perform(click())

        onView(withText("Contrasenya massa curta"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun successfulLogin() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SignInFragment())
                .commitNow()
        }

        onView(withId(R.id.username)).check(matches(isDisplayed()))
        onView(withId(R.id.username))
            .perform(typeText("Jan"))

        onView(withId(R.id.email))
            .perform(typeText("2223_jan.lazaro@iticbcn.cat"))

        onView(withId(R.id.password))
            .perform(typeText("rfewsfaefBHA1251"))

        closeSoftKeyboard()

        onView(withId(R.id.signin))
            .perform(click())

        onView(withText("Registre correcte"))
            .check(matches(isDisplayed()))
    }
}