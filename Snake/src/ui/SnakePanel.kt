package ui

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer

import kotlin.random.Random as Random

class SnakePanel : JPanel(), ActionListener {

	// user interface settings
    private var panelSize = 300		// in pixel
    private var dotSize = 10		// the size of each dot for the snake in pixel

	// snake settings
    private var snakeX = IntArray((panelSize/dotSize)*(panelSize/dotSize))
    private var snakeY = IntArray((panelSize/dotSize)*(panelSize/dotSize))
    private var snakeSize = 0		 		// snake size counted in dots
	private var snakeDirection = "right"	// right, left, up or down
	private var snakeDot: Image? = null
    private var snakeHead: Image? = null
	
	// apple settings
    private var appleX: Int = 0
    private var appleY: Int = 0
    private var apple: Image? = null
	
	// game settings
    private var gameTimer: Timer? = null	// if timer is running we are in "game mode",
											// if not "game over"
    private var gameSpeed = 140				// speed in ms
	private var gameInPause = false			// true to pause the game
	
    init {
        
		// init panel
        this.background = Color.black
        this.isFocusable = true
        this.preferredSize = Dimension(panelSize, panelSize)
        this.addKeyListener(TAdapter())
		
		// load images
		val iid = ImageIcon("src/res/dot.png")
        snakeDot = iid.image

        val iia = ImageIcon("src/res/apple.png")
        apple = iia.image

        val iih = ImageIcon("src/res/head.png")
        snakeHead = iih.image
		
		// init game
        initGame()
    }

    private fun initGame() {

		// default settings
        snakeSize = 3
		snakeDirection = "right"
		gameInPause = false

		// place the first snake dots
        for (z in 0 until snakeSize) {
            snakeX[z] = 50 - z * dotSize
            snakeY[z] = 50
        }

		// place the first apple
        placeApple()

		// init and start a game timer
        gameTimer = Timer(gameSpeed, this)
        gameTimer!!.start()
    }

    private fun move() {

		// move the tail
        for (z in snakeSize downTo 1) {
            snakeX[z] = snakeX[z - 1]
            snakeY[z] = snakeY[z - 1]
        }
		
		// move the head
		when(snakeDirection) {
			"left" -> snakeX[0] -= dotSize	// left
			"right" -> snakeX[0] += dotSize	// right
			"up" -> snakeY[0] -= dotSize		// up
			else -> snakeY[0] += dotSize		// down
		}
		
    }

    private fun checkCollision() {

		// snake eats itsself
        for (z in snakeSize downTo 1) {
			
            if (z > 4 && snakeX[0] == snakeX[z] && snakeY[0] == snakeY[z]) {
				gameTimer!!.stop()
				return
            }
        }

		// snake is out of board
        if (snakeY[0] >= panelSize || snakeY[0] < 0 || snakeX[0] >= panelSize || snakeX[0] < 0) {
            gameTimer!!.stop()
        }
    }

    private fun placeApple() {
		
		// calculate a new random x and y position for the apple
		val r = panelSize / dotSize
        appleX = Random.nextInt(0, r) * dotSize
        appleY = Random.nextInt(0, r) * dotSize
		
    }

	private fun checkApple() {

		// check if snake ate the apple
        if (snakeX[0] == appleX && snakeY[0] == appleY) {

            snakeSize++
            placeApple()
        }
    }
	
	public override fun paintComponent(g: Graphics) {
		
        super.paintComponent(g)

		// apint all component new
		if (!gameTimer!!.isRunning) {
			// draw "Game Over" if timer stopped bcs of collision
			drawString(g, "Game Over - Score: ${snakeSize}")
		} else if (gameInPause) {
			// draw "Pause" if timer is running but game is paused
			drawString(g, "Pause - current Score: ${snakeSize}")
		} else {
			// draw the apple
			g.drawImage(apple, appleX, appleY, this)
			// draw the snake
	        for (z in 0 until snakeSize) {
	            if (z == 0) {
					// draw the head
	                g.drawImage(snakeHead, snakeX[z], snakeY[z], this)
	            } else {
					// draw the tail
	                g.drawImage(snakeDot, snakeX[z], snakeY[z], this)
	            }
	        }
		}
    }

    private fun drawString(g: Graphics, s: String) {
    	
        val helvetica = Font("Helvetica", Font.BOLD, 14)
        val fontMetrics = getFontMetrics(helvetica)
		
		g.color = Color.white
        g.font = helvetica
		g.drawString(s, (panelSize - fontMetrics.stringWidth(s)) / 2, panelSize / 2)
    }
	
    override fun actionPerformed(e: ActionEvent) {

        if (!gameInPause) {

            checkApple()
            checkCollision()
            move()
        }

        repaint()
    }

    private inner class TAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {

            val key = e!!.keyCode

            if (key == KeyEvent.VK_LEFT && !snakeDirection.equals("right")) {
                snakeDirection = "left"
            } else if (key == KeyEvent.VK_RIGHT && !snakeDirection.equals("left")) {
                snakeDirection = "right"
            } else if (key == KeyEvent.VK_UP && !snakeDirection.equals("down")) {
                snakeDirection = "up"
            } else if (key == KeyEvent.VK_DOWN && !snakeDirection.equals("down")) {
                snakeDirection = "down"
            } else if (key == KeyEvent.VK_SPACE) {
				if (!gameTimer!!.isRunning) {
					initGame()
				} else {
					gameInPause = !gameInPause
				}
			}
        }
    }
}