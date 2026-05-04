package cm.horion.homegaz.domain.model.common

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
    val id: String
)