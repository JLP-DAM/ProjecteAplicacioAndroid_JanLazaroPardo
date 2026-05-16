package com.gilded

import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gilded.fragments.LoginFragment
import com.gilded.fragments.SignInFragment
import com.gilded.viewmodels.SignInViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Test
    fun emptyUsernameError() {

        lateinit var signInViewModel: SignInViewModel

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->

            signInViewModel = ViewModelProvider(activity)[SignInViewModel::class.java]

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

        assertEquals("Nom d'usuari invalid", signInViewModel.errorMessage.value)
    }

    @Test
    fun invalidPasswordError() {

        lateinit var signInViewModel: SignInViewModel

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->

            signInViewModel = ViewModelProvider(activity)[SignInViewModel::class.java]

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

        assertEquals("Contrasenya massa curta", signInViewModel.errorMessage.value)

    }

    @Test
    fun successfulSignIn() {

        lateinit var signInViewModel: SignInViewModel

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->
            signInViewModel = ViewModelProvider(activity)[SignInViewModel::class.java]

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

        assertNull(signInViewModel.errorMessage.value)

        activityScenario.onActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, LoginFragment())
                .commitNow()
        }

        onView(withId(R.id.login)).check(matches(isDisplayed()))
        onView(withId(R.id.email))
            .perform(typeText("2223_jan.lazaro@iticbcn.cat"))

        onView(withId(R.id.password))
            .perform(typeText("rfewsfaefBHA1251"))

        closeSoftKeyboard()

        onView(withId(R.id.login))
            .perform(click())
    }
}