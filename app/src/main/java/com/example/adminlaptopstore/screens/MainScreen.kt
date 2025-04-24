package com.example.adminlaptopstore.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.adminlaptopstore.navigation.BottomNavItem
import com.example.adminlaptopstore.viewmodel.CategoryViewModel
import com.example.adminlaptopstore.viewmodel.ProductViewModel

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation(backgroundColor = Color.White) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val bottomRoutes = listOf(
                    BottomNavItem.Category.route,
                    BottomNavItem.Product.route,
                    BottomNavItem.Order.route,
                    BottomNavItem.Settings.route,
                    BottomNavItem.Banner.route,
                    BottomNavItem.UserSc.route,
                    BottomNavItem.Warehouse.route,
                    BottomNavItem.Revenue.route
                )

                val shouldShowBottomNav = currentRoute?.let { route ->
                    bottomRoutes.any { route.contains(it) }
                } ?: true

                if (shouldShowBottomNav) {
                    listOf(
                        BottomNavItem.Category,
                        BottomNavItem.Product,
                        BottomNavItem.Order,
                        BottomNavItem.Settings
                    ).forEach { item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute?.contains(item.route) == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Category.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // CATEGORY
            composable(BottomNavItem.Category.route) {
                CategoryScreen(
                    onAddCategoryClick = {
                        navController.navigate("add_category")
                    },
                    onEditCategoryClick = { category ->
                        navController.navigate("edit_category/${category.id}")
                    }
                )
            }

            composable("add_category") {
                AddCategoryPage(navController = navController)
            }

            composable(
                route = "edit_category/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                EditCategoryScreen(
                    categoryId = categoryId,
                    onEditComplete = { navController.popBackStack() },
                    navController = navController
                )
            }

            // PRODUCT
            composable(BottomNavItem.Product.route) {
                ProductScreen(
                    onAddProductClick = {
                        navController.navigate("add_product")
                    },
                    onEditProductClick = { product ->
                        navController.navigate("edit_product/${product.productId}")
                    }
                )
            }

            composable("add_product") {
                AddProductScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "edit_product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val productViewModel = viewModel<ProductViewModel>()
                val categoryViewModel = viewModel<CategoryViewModel>()
                EditProductScreen(
                    productId = productId,
                    productViewModel = productViewModel,
                    categoryViewModel = categoryViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ORDER
            composable(BottomNavItem.Order.route) {
                OrderScreen(
                    onViewOrderDetails = { orderId ->
                        navController.navigate("order_details/${orderId}")
                    },
                    onUpdateOrderStatus = { orderId ->
                        navController.navigate("update_order_status/${orderId}")
                    }
                )
            }

            composable(
                route = "order_details/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailsScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onUpdateStatus = { id ->
                        navController.navigate("update_order_status/${id}")
                    }
                )
            }

            composable(
                route = "update_order_status/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                UpdateOrderStatusScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // SETTINGS
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(navController = navController)
            }

            composable(BottomNavItem.UserSc.route) { UserManagementScreen() }
            composable(BottomNavItem.Banner.route) { BannerScreen() }
            composable(BottomNavItem.Warehouse.route) { WarehouseScreen() }
            composable(BottomNavItem.Revenue.route) { RevenueScreen() }
        }
    }
}