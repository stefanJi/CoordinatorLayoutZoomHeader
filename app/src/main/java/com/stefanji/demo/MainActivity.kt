package com.stefanji.demo

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.stefanji.demo.databinding.ActivityMainBinding
import com.stefanji.demo.databinding.FragmentTemplateBinding
import com.stefanji.demo.view.AppBarLayout
import com.stefanji.demo.view.ViewOffsetBehavior
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var mViewPager: BannerViewPager<String>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = "$position"
        }.attach()

        mViewPager = binding.banner as BannerViewPager<String>
        mViewPager.adapter = BannerAdapter()
        mViewPager.setLifecycleRegistry(lifecycle)
        mViewPager.disallowParentInterceptDownEvent(true)
        mViewPager.create(listOf("", "", ""))

        setActivities()

        val behavior = (binding.banner.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior
        if (behavior is FlowAppBarZoomBehavior) {
            behavior.onBannerHide = {
                binding.titleBar.setBackgroundColor(Color.BLACK)
            }
            behavior.onBannerShow = {
                binding.titleBar.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }


    private fun setActivities() {
        binding.activity1.adapter = ActivityAdapter()
        binding.activity2.adapter = ActivityAdapter()
        binding.activity1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.activity2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.activity1.addItemDecoration(MDivider())
        binding.activity2.addItemDecoration(MDivider())
    }

}

class ActivityAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ActivityHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_activity, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 20

    class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

class PagerAdapter(fragmentManager: FragmentManager, lifeCycler: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifeCycler) {

    override fun getItemCount(): Int {
        return 20
    }

    override fun createFragment(position: Int): Fragment {
        return TemplatesFragment()
    }
}

class TemplatesFragment : Fragment() {

    private lateinit var binding: FragmentTemplateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentTemplateBinding.inflate(inflater).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.adapter = TemplateAdapter()
        binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rv.addItemDecoration(MDivider())
    }

    class TemplateAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return TemplateHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_home_template, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

        override fun getItemCount(): Int = 20
    }

    class TemplateHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

class MDivider : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(20, 10, 20, 10)
    }
}

class BannerAdapter : BaseBannerAdapter<String>() {

    override fun bindData(holder: BaseViewHolder<String>, data: String?, position: Int, pageSize: Int) {
        val iv = holder.findViewById<ImageView>(R.id.ivBanner)
        iv.setImageResource(R.drawable.header)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_home_banner
    }

}

class MCoordinatorLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return super.onStartNestedScroll(child, target, axes, type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(target, dx, dy, consumed, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
    }

}

class MAppBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppBarLayout(context, attrs, defStyleAttr) {

    override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray?) {
        super.onNestedPreScroll(target, dx, dy, consumed)
    }

    override fun onStartNestedScroll(child: View?, target: View?, nestedScrollAxes: Int): Boolean {
        val can = super.onStartNestedScroll(child, target, nestedScrollAxes)
        return can
    }

    override fun onNestedScroll(target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        Log.d("JY", "appbar onInterceptTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return super.onInterceptTouchEvent(ev)
//            .also { Log.d("JY", "appbar result: $it") }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        Log.d("JY", "appbar onTouchEvent ${MotionEvent.actionToString(event.action)}")
        return super.onTouchEvent(event)
//            .also { Log.d("JY", "appbar result: $it") }
    }

}

class MCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyleAttr) {

    override fun onStartNestedScroll(child: View?, target: View?, nestedScrollAxes: Int): Boolean {
        val can = super.onStartNestedScroll(child, target, nestedScrollAxes)
        return can
    }

    override fun onNestedScroll(target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        Log.d("JY", "collapsing onInterceptTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return super.onInterceptTouchEvent(ev)
//            .also { Log.d("JY", "collapsing result: $it") }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        Log.d("JY", "collapsing onTouchEvent ${MotionEvent.actionToString(event.action)}")
        return super.onTouchEvent(event)
//            .also { Log.d("JY", "collapsing result: $it") }
    }

}

class MLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        Log.d("JY", "linear onInterceptTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return super.onInterceptTouchEvent(ev)
//            .also { Log.d("JY", "linear ${MotionEvent.actionToString(ev.action)} result: $it") }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        Log.d("JY", "linear onTouchEvent ${MotionEvent.actionToString(event.action)}")
        return super.onTouchEvent(event)
//            .also { Log.d("JY", "linear ${MotionEvent.actionToString(event.action)} result: $it") }
    }
}

class MScrollViewBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.ScrollingViewBehavior(context, attrs) {

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        Log.d("JY", "${javaClass.simpleName} onInterceptTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return super.onInterceptTouchEvent(parent, child, ev)
            .also { Log.d("JY", "${javaClass.simpleName} ${MotionEvent.actionToString(ev.action)} result: $it") }
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        Log.d("JY", "${javaClass.simpleName} onTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return super.onTouchEvent(parent, child, ev)
            .also { Log.d("JY", "behavior ${MotionEvent.actionToString(ev.action)} result: $it") }
    }
}

class AppBarBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context, attrs) {

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        Log.d("JY", "barbehavior onInterceptTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return super.onInterceptTouchEvent(parent, child, ev)
//            .also { Log.d("JY", "barbehavior ${MotionEvent.actionToString(ev.action)} result: $it") }
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        Log.d("JY", "barbehavior onTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return super.onTouchEvent(parent, child, ev)
//            .also { Log.d("JY", "barbehavior ${MotionEvent.actionToString(ev.action)} result: $it") }
    }
}

class FlowAppBarZoomBehavior(context: Context, attrs: AttributeSet) : ViewOffsetBehavior<View>(context, attrs) {

    var onBannerHide = {}
    var onBannerShow = {}

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return (dependency.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior is AppBarBehavior
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        Log.d("JYJY", "onDepChanged ${dependency.top} ${dependency.bottom}")
        if (dependency.top > 0) {
            //下拉放大
            val cur = child.height
            val scale = (dependency.top.toFloat() + cur) / cur
            child.scaleY = scale
            child.scaleX = scale
            topAndBottomOffset = dependency.top / 2
        } else {
            //正常区域内的上滑和下滑
            topAndBottomOffset = dependency.top
            val offset = abs(dependency.top)
            if (offset >= child.height) {
                //全部隐藏了
                onBannerHide()
            } else if (offset < child.height) {
                onBannerShow()
            }
        }
        return false
    }
}