package stepdefinitions;

import base.BaseTest;
import io.cucumber.java.*;

public class Hooks {

    @Before
    public void setup() {
        BaseTest.initDriver();   // 👈 Driver created here
    }

    @After
    public void tearDown() {
        BaseTest.quitDriver();   // 👈 Driver closed here
    }
}