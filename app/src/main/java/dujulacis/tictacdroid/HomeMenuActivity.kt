package dujulacis.tictacdroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dujulacis.tictacdroid.databinding.ActivityHomeMenuBinding
import kotlin.system.exitProcess

class HomeMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Let player choose to play PvP or PvC
        binding.playButton.setOnClickListener {
            val options = arrayOf("Player vs Player", "Player vs Computer")

            AlertDialog.Builder(this)
                .setTitle("Choose Game Mode")
                .setItems(options) { _, which ->
                    val isComputer = (which == 1)

                    // If computer mode chosen, let player choose their name (defaults to Player)
                    if (isComputer) {
                        val editText = android.widget.EditText(this)
                        editText.hint = "Enter your name"

                        AlertDialog.Builder(this)
                            .setTitle("Enter Player Name")
                            .setView(editText)
                            .setPositiveButton("Start Game") { _, _ ->
                                val playerName = editText.text.toString().ifBlank { "Player" }
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("isComputer", true)
                                intent.putExtra("playerName", playerName)
                                startActivity(intent)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()

                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("isComputer", false)
                        startActivity(intent)
                    }
                }
                .show()
        }

        // Exit button
        binding.homeExitButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit the game?")
                .setPositiveButton("Yes") { _, _ ->
                    exitProcess(0)
                }
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show()
        }


    }
}