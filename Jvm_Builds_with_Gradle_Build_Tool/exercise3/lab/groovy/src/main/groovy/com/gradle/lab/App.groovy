/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package com.gradle.lab

//import com.gradle.lab.old.OldMessage

class App {

    private static Message HELLO = new Message("Hello World!")

    String getGreeting() {
        return HELLO.getTextWithTime()
    }

    static void main(String[] args) {
        println new App().greeting
        println HELLO.hasWhitespace()
        println HELLO.getTextHash()
        //println(new OldMessage("hello there").hasWhitespace())
    }
}
