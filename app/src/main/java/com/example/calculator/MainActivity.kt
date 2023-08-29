package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isAddOperationAllowed = false
    private var isDecimalAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (isDecimalAllowed)
                    workingsTV.append(view.text)

                isDecimalAllowed = false
            } else
                workingsTV.append(view.text)

            isAddOperationAllowed = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && isAddOperationAllowed) {
            workingsTV.append(view.text)
            isAddOperationAllowed = false
            isDecimalAllowed = true
        }
    }

    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = workingsTV.length()
        if (length > 0)
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = getDigitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = calculateTimesDivision(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = calculateAddSubtract(timesDivision)
        return result.toString()
    }

    private fun calculateAddSubtract(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun calculateTimesDivision(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('×') || list.contains('/')) {
            list = calculateTimesDiv(list)
        }
        return list
    }

    private fun calculateTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '×' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun getDigitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingsTV.text) {
            if (character.isDigit() || character == '.')
                currentDigit += character
            else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }
}
