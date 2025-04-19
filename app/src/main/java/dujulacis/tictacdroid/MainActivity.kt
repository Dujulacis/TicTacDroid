package dujulacis.tictacdroid

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dujulacis.tictacdroid.databinding.ActivityMainBinding

// TODO: Choose starting symbol at the beginning. Implement better CPU logic.

// Code modified: https://www.youtube.com/watch?v=POFvcoRo3Vw

class MainActivity : AppCompatActivity() {

    // INIT //

    // Set default computer mode as false
    private var isComputerMode = false

    // Initialize default player name
    private var playerName: String = "Player"

    // Will prevent player from moving, when computer is
    private var isCPUMoving = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isComputerMode = intent.getBooleanExtra("isComputer", false)
        playerName = intent.getStringExtra("playerName") ?: "Player"

        initBoard()
        setTurnLabel()
    }

    enum class Turn
    {
        CIRCLE,
        CROSS
    }

    private var firstTurn = Turn.CROSS
    private var currentTurn = Turn.CROSS

    private var crossesScore = 0
    private var circlesScore = 0

    private var boardList = mutableListOf<Button>()

    private lateinit var binding : ActivityMainBinding

    companion object
    {
        const val CIRCLE = "O"
        const val CROSS = "X"
    }

    private fun initBoard() 
    {
        boardList.add(binding.a1)
        boardList.add(binding.a2)
        boardList.add(binding.a3)
        boardList.add(binding.b1)
        boardList.add(binding.b2)
        boardList.add(binding.b3)
        boardList.add(binding.c1)
        boardList.add(binding.c2)
        boardList.add(binding.c3)
    }


    // FUNCTIONS //

    // Check if a button is tapped on the game board
    fun boardTapped(view: View) {
        if (isCPUMoving) return
        if (view !is Button) return
        if (view.text != "") return

        addToBoard(view)

        if (checkForVictory(CROSS)) {
            crossesScore++
            result("Crosses Win!")
            return
        }

        else if (checkForVictory(CIRCLE)) {
            circlesScore++
            result("Circles Win!")
            return
        }

        if (fullBoard()) {
            result("Draw")
            return
        }

        if (isComputerMode && currentTurn == Turn.CIRCLE) {
            computerMove()
        }
    }

    // Currently "computer" chooses randomly, wherever there is empty buttons. Simulated delay applied.
    private fun computerMove() {

        val emptyButtons = boardList.filter { it.text == "" }

        if (emptyButtons.isNotEmpty()) {
            isCPUMoving = true
            val randomButton = emptyButtons.random()
            randomButton.postDelayed({
                addToBoard(randomButton)

                if (checkForVictory(CIRCLE)) {
                    circlesScore++
                    result("Circles Win!")
                    return@postDelayed
                }

                if (checkForVictory(CROSS)) {
                    crossesScore++
                    result("Crosses Win!")
                    return@postDelayed
                }

                if (fullBoard()) {
                    result("Draw")
                }

                isCPUMoving = false
            }, 1000)
        }
    }


    // Checks for victory conditions (horizontal, vertical and diagonal match)
    private fun checkForVictory(s: String): Boolean {

        // Horizontal match
        if (match(binding.a1, s) && match(binding.a2, s) && match(binding.a3, s))
            return true

        if (match(binding.b1, s) && match(binding.b2, s) && match(binding.b3, s))
            return true

        if (match(binding.c1, s) && match(binding.c2, s) && match(binding.c3, s))
            return true

        // Vertical match
        if (match(binding.a1, s) && match(binding.b1, s) && match(binding.c1, s))
            return true

        if (match(binding.a2, s) && match(binding.b2, s) && match(binding.c2, s))
            return true

        if (match(binding.a3, s) && match(binding.b3, s) && match(binding.c3, s))
            return true

        // Diagonal match
        if (match(binding.a1, s) && match(binding.b2, s) && match(binding.c3, s))
            return true

        if (match(binding.c1, s) && match(binding.b2, s) && match(binding.a3, s))
            return true

        return false
    }

    // Check if button text matches with symbol
    private fun match(button: Button, symbol : String): Boolean = button.text == symbol

    // Function to display result message
    private fun result(title: String)
    {
        var message = "\nCrosses $crossesScore\n\nCircles $circlesScore"
        if (isComputerMode)
            message = "\n$playerName $crossesScore\n\nCPU $circlesScore"
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("New Game")
            { _,_ ->
                resetBoard()
            }
            .setNeutralButton("Main Menu")
            { _, _ ->
                finish()
            }
            .setNegativeButton("Exit Game")
            { _, _ ->
                finishAffinity()
            }
            .setCancelable(false)
            .show()
    }

    // Function to reset board and switch starting symbol/player
    private fun resetBoard()
    {
        for (button in boardList)
        {
            button.text = ""
        }

        if (firstTurn == Turn.CROSS)
            firstTurn = Turn.CIRCLE
        else if (firstTurn == Turn.CIRCLE)
            firstTurn = Turn.CROSS

        currentTurn = firstTurn
        setTurnLabel()
        if (isComputerMode && currentTurn == Turn.CIRCLE) {
            computerMove()
        }
    }

    // Check if all buttons have any text, meaning the board is full
    private fun fullBoard(): Boolean
    {
        for (button in boardList)
        {
            if(button.text == "")
                return false
        }
            return true
    }

    // Turn logic
    private fun addToBoard(button: Button)
    {
        // Only allows choosing empty buttons
        if (button.text != "")
            return

        if (currentTurn == Turn.CIRCLE)
        {
            button.text = CIRCLE
            currentTurn = Turn.CROSS
        }
        else if (currentTurn == Turn.CROSS)
        {
            button.text = CROSS
            currentTurn = Turn.CIRCLE
        }
        setTurnLabel()
    }

    // Turn label logic
    private fun setTurnLabel() 
    {
        var turnText = ""
        if(currentTurn == Turn.CIRCLE)
            if (isComputerMode)
                turnText = "Turn CPU ($CIRCLE)"
            else
                turnText = "Turn $CIRCLE"
        else if(currentTurn == Turn.CROSS)
            if (isComputerMode)
                turnText = "Turn $playerName ($CROSS)"
            else
                turnText = "Turn $CROSS"
        binding.turnTV.text = turnText
    }

}