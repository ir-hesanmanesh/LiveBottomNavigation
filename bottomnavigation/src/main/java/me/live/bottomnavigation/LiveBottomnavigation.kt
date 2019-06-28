package me.live.bottomnavigation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView

class LiveBottomnavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    styleAttr: Int = 0
) : LinearLayout(context, attrs, styleAttr) {

    /** LiveParams LiveBottomNavigation knows about how the item will render **/
    private val params = LiveParams()


    /** The item that is active and chosen **/

    private val navigationItems = arrayListOf<LiveNavigation>()

    /** The item position that is active and chosen **/
    private var selectPositon: Int = 0

    /** For the value of view's getDrawingRect **/
    private val rect = Rect()

    /** For the value of view's getlocusLocationScreen **/
    private val loccus = IntArray(2)

    /** If an item touched and released outside this flag will be false to prevent double animation **/
    private var needsToScale = true

    /** The listener that triggered when active navigation item changed **/
    private var itemChangeListener: OnLiveNavigationItemChangeListener? = null

    /** A data class for LiveNavigation item **/
    data class LiveNavigation(val position: Int, val view: View, val textView: TextView, val imageView: ImageView)

    /** Initialize LiveBottomNavigation by getting the values from xml **/

    init {
        @SuppressLint("Recycle", "CustomViewStyleable")
        val attr = context.obtainStyledAttributes(attrs, R.styleable.LiveBottomNavigation)
        if (attr.hasValue(R.styleable.LiveBottomNavigation_menu)) {
            val popMenu = PopupMenu(context, null)
            val menu = popMenu.menu
            MenuInflater(context).inflate(attr.getResourceId(R.styleable.LiveBottomNavigation_menu, 0), menu)
            params.menu = menu
        }
        if (attr.hasValue(R.styleable.LiveBottomNavigation_active_color)) {
            val activeColor = attr.getColor(R.styleable.LiveBottomNavigation_active_color, 0)
            params.activeColor = activeColor
        }

        if (attr.hasValue(R.styleable.LiveBottomNavigation_passive_color)) {
            val passiveColor = attr.getColor(R.styleable.LiveBottomNavigation_passive_color, 0)
            params.passiveColor = passiveColor
        }

        if (attr.hasValue(R.styleable.LiveBottomNavigation_pressed_color)) {
            val pressedColor = attr.getColor(R.styleable.LiveBottomNavigation_pressed_color, 0)
            params.pressedColor = pressedColor
        }
        if (attr.hasValue(R.styleable.LiveBottomNavigation_item_padding)) {
            val px = attr.getDimension(R.styleable.LiveBottomNavigation_item_padding, 0f)
            params.itemPadding = px
        }
        if (attr.hasValue(R.styleable.LiveBottomNavigation_item_text_size)) {
            val pxSize = attr.getDimensionPixelSize(R.styleable.LiveBottomNavigation_item_text_size, 0)
            params.itemTextSize = pxSize.toFloat()
        }
        if (attr.hasValue(R.styleable.LiveBottomNavigation_animation_duration)) {
            val animDuration = attr.getInteger(R.styleable.LiveBottomNavigation_animation_duration, 0)
            params.animationDuraction = animDuration
        }
        if (attr.hasValue(R.styleable.LiveBottomNavigation_scale_percent)) {
            val scalePercent = attr.getInteger(R.styleable.LiveBottomNavigation_scale_percent, 0)
            params.endScale = 1 - scalePercent.toFloat() / 100
        }

        attr.recycle()
        prepareView()
    }

    /** For setting OnNavigationItemChangeListener **/
    public fun setOnNavigationItemChangedListener(listener: OnLiveNavigationItemChangeListener) {
        this.itemChangeListener = listener
    }

    /** Inflates NavigationItem view and fills it with attrs params **/
    private fun prepareView() {
        val menu = if (params.menu == null) {
            throw NoSuchFieldException("LiveBottomNavigation: You need to declare app:menu in xml ")
        } else {
            params.menu!!
        }

        for (index in 0 until menu.size()) {
            //Prepare navigation item to display and get items view references
            val navigationItem = menu.getItem(index)
            val navigationItemView = LayoutInflater.from(context).inflate(R.layout.item_live, this, false)
            val imageView = navigationItemView.findViewById<ImageView>(R.id.navigation_item_image)
            val textView = navigationItemView.findViewById<TextView>(R.id.navigation_item_text)

            // set item icon and title
            imageView.setImageDrawable(navigationItem.icon)
            textView.text = navigationItem.title

            // Set navigation item's color. If it's pressed then color it with active color
            var navigationItemColor: Int
            if (navigationItem.isCheckable) {
                navigationItemColor = params.activeColor
                selectPositon = index
            } else {
                navigationItemColor = params.passiveColor
            }
            textView.setTextColor(navigationItemColor)
            imageView.setColorFilter(navigationItemColor)

            // set text and image view size
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.itemTextSize)

            //set navigation items padding
            val padding = params.itemPadding.toInt()
            navigationItemView.setPadding(padding, padding, padding, padding)

            //Add navigation item to list
            val item = LiveNavigation(index, navigationItemView, textView, imageView)
            navigationItems.add(item)

            // Set a touch listener to navigation item
            addListener(item)

            // Add navigation item to view.
            val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.weight = 1f
            params.gravity = Gravity.CENTER
            this.addView(navigationItemView, params)
        }
    }

    /** Listener to handle click and release events for navigation item **/
    @SuppressLint("ClickableViewAccessibility")
    private fun addListener(navigationItem: LiveNavigation) {
        navigationItem.view.setOnTouchListener { _, event ->
            // If user touched to item and then moved finger to outside of the item, make view how it looks was before touching
            // This method will fire lots of times. So if you scale the view to how it looks was before once, then no need to scale anymore.
            if (isTouchingOutsideOfItem(navigationItem.view, event.rawX.toInt(), event.rawY.toInt()) && needsToScale) {
                scaleNavigationItem(navigationItem, params.endScale, params.startScale)
                colorAnim(navigationItem, false)
                needsToScale = false
                Log.w("LOG", "out")
            }
            /** If navigation item is pressed and touched position is not outside of item view make animations **/
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (needsToScale) {
                    scaleNavigationItem(navigationItem, params.startScale, params.endScale)
                    colorAnim(navigationItem, true)
                    Log.w("LOG", "down")
                }
                needsToScale = true
            }
            /** If navigation item is released and touched position is not outside of item view make animations **/
            else if (event.action == MotionEvent.ACTION_UP) {
                if (needsToScale) {
                    scaleNavigationItem(navigationItem, params.endScale, params.startScale)
                    selectItem(navigationItem, true)
                    colorAnim(navigationItem, false)
                    Log.e("LOG", "up")
                }
                needsToScale = true
            }
            true
        }
    }

    /** Selects the given navigation item and updates current navigation item **/
    private fun selectItem(navigationItem: LiveBottomnavigation.LiveNavigation, ClickSound: Boolean) {
        /** Get previous selected item and color it passive **/
        val selectedNavigationItem = navigationItems[selectPositon]
        selectedNavigationItem.textView.setTextColor(params.passiveColor)
        selectedNavigationItem.imageView.setColorFilter(params.passiveColor)
        /** click sound **/
        if (ClickSound)
            navigationItem.view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
        /** update select position **/
        selectPositon = navigationItem.position
        /**  Set recently selected item and color it active **/
        navigationItem.textView.setTextColor(params.activeColor)
        navigationItem.imageView.setColorFilter(params.activeColor)
        /** Notify active view changed **/
        itemChangeListener?.onLiveNavigationItemChanged(navigationItem)
    }

    /** If active navigation item need to change programmatically, this method will do it  **/
    fun setActiveNavigationIndex(index: Int) {
        selectItem(navigationItems[index], false)
    }


    private fun scaleNavigationItem(
        navigationItem: LiveNavigation,
        startScale: Float,
        endScale: Float
    ) {
        val animation = ScaleAnimation(
            startScale, endScale, startScale, endScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        )
        animation.fillAfter = true
        animation.duration = params.animationDuraction.toLong()
        navigationItem.view.startAnimation(animation)
    }

    /** Determines if touch event exceed the last pressed navigation item **/
    private fun isTouchingOutsideOfItem(view: View, x: Int, y: Int): Boolean {
        view.getDrawingRect(rect)
        view.getLocationOnScreen(loccus)
        rect.offset(loccus[0], loccus[1])
        return !rect.contains(x, y)
    }

    /** click and release color animation  **/
    private fun colorAnim(navigationItem: LiveNavigation, activeToPassive: Boolean) {
        /** If view is selected then color it with active color, if it is not, color view it with passive color **/
        val colorActive = if (navigationItem.view == navigationItems[selectPositon].view)
            params.activeColor
        else params.passiveColor
        val colorPassive = params.pressedColor
        /** If the transition between activeToPassive or passiveToActive **/
        val colorFrom = if (activeToPassive) colorActive else colorPassive
        var colorTo = if (activeToPassive) colorPassive else colorActive
        /** If view is selected one and touched finger released from selected navigation item, color it active **/
        if (navigationItem.view == navigationItems[selectPositon].view && !activeToPassive) {
            colorTo = params.activeColor
        }
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = params.animationDuraction.toLong()
        colorAnimation.addUpdateListener { animator ->
            val navigationItemColor = animator.animatedValue as Int
            navigationItem.textView.setTextColor(navigationItemColor)
            navigationItem.imageView.setColorFilter(navigationItemColor)
        }
        colorAnimation.start()
    }

}
