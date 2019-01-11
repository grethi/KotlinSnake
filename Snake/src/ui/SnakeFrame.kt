package ui

import java.awt.EventQueue
import javax.swing.JFrame

class SnakeFrame : JFrame() {

    init {

        initUserInterface()
    }

    private fun initUserInterface() {

        add(SnakePanel())

        title = "Snake"
        isResizable = false
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
		
        pack()
		
		setLocationRelativeTo(null)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            EventQueue.invokeLater {
                val frame = SnakeFrame()
                frame.isVisible = true
            }
        }
    }
}