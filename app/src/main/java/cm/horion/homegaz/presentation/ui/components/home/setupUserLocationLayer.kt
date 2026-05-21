package cm.horion.homegaz.presentation.ui.components.home

import android.graphics.PointF
import cm.horion.homegaz.R
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


fun setupUserLocationLayer(
    mv                : MapView,
    userLocationLayer : UserLocationLayer
) {
    userLocationLayer.isVisible           = true
    userLocationLayer.isHeadingModeActive   = true

    userLocationLayer.setObjectListener(object : UserLocationObjectListener {

        override fun onObjectAdded(view: UserLocationView) {
            //  Centrer la caméra sur l'utilisateur la première fois
            userLocationLayer.setAnchor(
                PointF((mv.width * 0.5).toFloat(), (mv.height * 0.5).toFloat()),
                PointF((mv.width * 0.5).toFloat(), (mv.height * 0.83).toFloat())
            )

            view.pin.useCompositeIcon()
            view.pin.setIcon(
                ImageProvider.fromResource(mv.context, R.drawable.profil),
                com.yandex.mapkit.map.IconStyle().apply {
                    anchor = PointF(0.5f, 0.5f)
                    scale  = 0.9f
                }
            )
        }

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) = Unit
    })
}