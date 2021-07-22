package ch.wenksi.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    var lastNumeric: Boolean = false
    var lastDot: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onDigit(view: View) {
        if (tvInput.text.toString() == "Infinity")
            onClear(view)
        val btnText = (view as Button).text
        lastNumeric = true
        tvInput.append(btnText)
    }

    fun onClear(view: View) {
        lastNumeric = false
        lastDot = false
        tvInput.text = ""
    }

    fun onDecimal(view: View) {
        if(lastNumeric && !lastDot) {
            tvInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onEqual(view: View) {
        if(!lastNumeric) return
        var tvValue = tvInput.text.toString()
        var prefix = ""
        try {
            if(tvValue.startsWith("-")) {
                prefix = "-"
                tvValue = tvValue.substring(1)
            }

            if(tvValue.contains("-")) calculate(tvValue, "-", prefix, ::subtract)
            else if(tvValue.contains("+")) calculate(tvValue, "+", prefix, ::add)
            else if(tvValue.contains("*")) calculate(tvValue, "*", prefix, ::multiply)
            else if(tvValue.contains("/")) calculate(tvValue, "/", prefix, ::divide)
        } catch(e: ArithmeticException) {
            e.printStackTrace()
        }
    }

    fun onOperator(view: View) {
        if (lastNumeric && !isOperatorAdded(tvInput.text.toString())) {
            tvInput.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    private fun isOperatorAdded(value: String): Boolean {
        return if (value.startsWith("-")) {
            false
        } else {
            value.contains("+")
                    || value.contains("*")
                    || value.contains("-")
                    || value.contains("/")
        }
    }

    private fun calculate(tvValue: String, operator: String, prefix: String, calculation: (a: Double, b: Double) -> Double) {
        if (!"*+-/".contains(operator)) throw IllegalArgumentException("Char '$operator' is not a valid operator")

        val splitValue = tvValue.split(operator)
        var one = splitValue[0]
        var two = splitValue[1]

        if(!prefix.isEmpty()) {
            one = prefix + one
        }
        val result = calculation(one.toDouble(), two.toDouble()).toString()
        tvInput.text = removeZeroAfterDot(result)
    }

    private fun add(a: Double, b: Double): Double = a + b

    private fun multiply(a: Double, b: Double): Double = a * b

    private fun subtract(a: Double, b: Double): Double = a - b

    private fun divide(a: Double, b: Double): Double {
        if (b == 0.0) {
            tvInput.text = "Infinity"
            lastNumeric = false
            throw ArithmeticException("Division by zero")
        }
        return a / b
    }

    private fun removeZeroAfterDot(result: String): String {
        var value = result
        if(result.contains(".0"))
            value = result.substring(0, result.length - 2)
        return value
    }
}
