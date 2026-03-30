package cm.horion.homegaz

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDark = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val bgColor = if (isDark) 0xFF001F2B.toInt() else 0xFFFFFFFF.toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val centerZone = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }

        val logo = ImageView(this).apply {
            setImageResource(if (isDark) R.drawable.logoblanc else R.drawable.ic_launcher_playstore)
            layoutParams = LinearLayout.LayoutParams(180.dp, 180.dp)
        }
        centerZone.addView(logo)

        // Zone Branding (En bas)
        val brandingZone = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60.dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val branding = ImageView(this).apply {
            setImageResource(if (isDark) R.drawable.by_orion_white else R.drawable.by_orion)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 30.dp)
        }
        brandingZone.addView(branding)

        root.addView(centerZone)
        root.addView(brandingZone)
        setContentView(root)
        lifecycleScope.launch {
            delay(1200)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)

            finish()
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}