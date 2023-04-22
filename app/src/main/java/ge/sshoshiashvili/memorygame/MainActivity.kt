package ge.sshoshiashvili.memorygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    /** Amount of flipped cards */
    private var flipped: Int = 0

    /** Successful attempts */
    private var success: Int = 0

    /** Failed attempts */
    private var fail: Int = 0

    /** Drawable IDs for card frontsides */
    private val names = listOf(R.drawable.kabosu, R.drawable.fortnite_man, R.drawable.xe)

    /** Drawable ID for card backside */
    private val cardBack = R.drawable.card_bakh

    /** actual IDs of each card */
    private val realCards = MutableList(7) { 5 }

    /** Whether each card is flipped or not, appears or not */
    private val flipState = MutableList(7) { false }
    private val appearState = MutableList(7) { true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resetCards()
        randomizePictures()
        renderCardsVisibility()
        updateTexts(false, false)
        addListeners()
    }

    /**
     * Randomly assigns pictures to cards
     * */
    private fun randomizePictures() {
        val chosenAmnts = MutableList(names.size) { 0 }

        for (i in 1..6) {
            var randomIndex = Random.nextInt(names.size)
            while (chosenAmnts[randomIndex] == 2)
                randomIndex = Random.nextInt(names.size)
            chosenAmnts[randomIndex]++
            val randomElement = names[randomIndex]
            realCards[i] = randomElement
        }
    }

    /**
     * Updates texts according to the current state.
     * If called after a success, success will be green.
     * If called after failure, fail will be red.
     * Otherwise, both will be gray.
     * */
    private fun updateTexts(success: Boolean, fail: Boolean) {
        val succ = findViewById<TextView>(R.id.successView)
        val fa = findViewById<TextView>(R.id.failView)

        succ.setText(getString(R.string.success) + " " + this.success)
        fa.setText(getString(R.string.fail) + " " + this.fail)

        succ.setTextColor(ContextCompat.getColor(this, R.color.gray))
        fa.setTextColor(ContextCompat.getColor(this, R.color.gray))

        if (success)
            succ.setTextColor(ContextCompat.getColor(this, R.color.green))
        if (fail)
            fa.setTextColor(ContextCompat.getColor(this, R.color.red))
    }

    /**
     * Puts card with frontside up
     * */
    private fun flip(index: Int) {
        flipState[index] = true
        val resourceId = resources.getIdentifier("imageView$index", "id", packageName)
        val imageView = findViewById<ImageView>(resourceId)
        imageView.setImageResource(realCards[index])
    }

    /**
     * Puts card with backside up
     * */
    private fun unflip(index: Int) {
        flipState[index] = false
        val resourceId = resources.getIdentifier("imageView$index", "id", packageName)
        val imageView = findViewById<ImageView>(resourceId)
        imageView.setImageResource(cardBack)
    }

    /**
     * Updates cards being shown based on current state
     * */
    private fun renderCardsVisibility() {
        for (i in 1..6) {
            val resourceId = resources.getIdentifier("imageView$i", "id", packageName)
            val imageView = findViewById<ImageView>(resourceId)

            if (appearState[i])
                imageView.visibility = View.VISIBLE
            else
                imageView.visibility = View.INVISIBLE
        }
    }

    /**
     * Removes the chosen card
     * */
    private fun hideCard(index: Int) {
        appearState[index] = false
        renderCardsVisibility()
    }

    /**
     * Puts the chosen card back
     * */
    private fun showCard(index: Int) {
        appearState[index] = true
        renderCardsVisibility()
    }

    /**
     * Action taken when a card is clicked.
     * Touched card is flipped.
     * If there was one card flipped already, checks if the cards match and updates success/fail count accordingly.
     * If there was none flipped, nothing.
     * If there were two cards flipped, they'll either be removed if they match or flipped back if they don't.
     * */
    private fun punchCard(index: Int) {
        if (!flipState[index]) {
            if (flipped == 2 || flipped == 0) {
                if (flipped == 2) {
                    // initial value doesn't matter
                    var obam = 500
                    for (j in 1..6) {
                        if (flipState[j]) {
                            if (obam == 500) {
                                obam = j
                            } else {
                                if (realCards[j] == realCards[obam]) {
                                    hideCard(obam)
                                    hideCard(j)
                                    renderCardsVisibility()
                                }
                            }
                        }
                    }
                }
                for (j in 1..6) {
                    if (index != j)
                        unflip(j)
                }
                flipped = 1
                flip(index)
                updateTexts(false, false)
            } else {// if (flipped == 1) {
                for (j in 1..6) {
                    if (j != index && flipState[j]) {
                        if (realCards[index] == realCards[j]) {
                            success++
                            updateTexts(true, false)
                            flip(index)
                            renderCardsVisibility()
                        } else {
                            fail++
                            updateTexts(false, true)
                            flip(index)
                            renderCardsVisibility()
                        }
                    }
                }
                flipped = 2
            }
        }
    }

    /**
     * Add onclick listeners to the cards and button
     * */
    private fun addListeners() {
        for (i in 1..6) {
            val resourceId = resources.getIdentifier("imageView$i", "id", packageName)
            val imageView = findViewById<ImageView>(resourceId)

            imageView.setOnClickListener {
                punchCard(i)
            }
        }

        val myButton = findViewById<Button>(R.id.button)
        myButton.setOnClickListener { restart() }
    }

    /**
     * Displays all cards, with backside towards the player.
     * */
    private fun resetCards() {
        for (i in 1..6) {
            unflip(i)
            showCard(i)
            flipped = 0
        }
    }

    /**
     * Shuffles the cards and puts them with backside to the player
     * */
    private fun restart() {
        resetCards()
        randomizePictures()
        renderCardsVisibility()
    }
}