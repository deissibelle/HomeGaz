package cm.horion.homegaz.domain.model.common

 data class NavItem(
    val Id: String,
    val labelResId: Int,
    val iconOutlined: androidx.compose.ui.graphics.vector.ImageVector,
    val iconFilled: androidx.compose.ui.graphics.vector.ImageVector
)