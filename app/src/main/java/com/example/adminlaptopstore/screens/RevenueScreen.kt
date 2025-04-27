    package com.example.adminlaptopstore.screens

    import android.graphics.Color
    import android.os.Build
    import androidx.annotation.RequiresApi
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.itemsIndexed
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.AttachMoney
    import androidx.compose.material.icons.filled.CalendarMonth
    import androidx.compose.material.icons.filled.ReceiptLong
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.adminlaptopstore.ViewModel.RevenueViewModel
    import com.example.adminlaptopstore.model.OrderDataModels
    import java.util.*
    import com.github.mikephil.charting.charts.BarChart
    import com.github.mikephil.charting.data.*
    import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
    import androidx.compose.ui.viewinterop.AndroidView
    import com.github.mikephil.charting.components.XAxis
    import com.github.mikephil.charting.formatter.ValueFormatter
    import java.time.LocalDate
    import java.time.format.DateTimeFormatter
    import kotlinx.coroutines.launch

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun RevenueScreen(viewModel: RevenueViewModel = viewModel()) {
        val isLoading by viewModel.isLoading.collectAsState()
        val totalRevenue by viewModel.totalRevenue.collectAsState()
        val orderCount by viewModel.orderCount.collectAsState()
        val deliveredOrders by viewModel.deliveredOrders.collectAsState()
        val revenueData by viewModel.revenueData.collectAsState()
        val scope = rememberCoroutineScope()

        // Initialize with current month range
        val today = LocalDate.now()
        val monthStart = today.withDayOfMonth(1)
        val monthEnd = today.withDayOfMonth(today.month.length(today.isLeapYear))

        var startDate by remember {
            mutableStateOf(monthStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        }
        var endDate by remember {
            mutableStateOf(monthEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        }
        var filterMode by remember { mutableStateOf("date_range") } // "date_range" or "year"
        var selectedYear by remember { mutableStateOf(LocalDate.now().year.toString()) }

        val years = (2020..LocalDate.now().year).map { it.toString() }.reversed()

        // State for dropdown
        var yearDropdownExpanded by remember { mutableStateOf(false) }

        // Apply initial filter when composable is first created
        LaunchedEffect(Unit) {
            viewModel.applyDateFilter(startDate, endDate)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Báo Cáo Doanh Thu",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filter options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Filter mode selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = filterMode == "date_range",
                            onClick = { filterMode = "date_range" },
                            label = { Text("Theo khoảng ngày") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = "Calendar",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        FilterChip(
                            selected = filterMode == "year",
                            onClick = { filterMode = "year" },
                            label = { Text("Theo năm") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = "Year",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filter controls
                    when (filterMode) {
                        "date_range" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DatePickerField(
                                        label = "Từ ngày",
                                        date = startDate,
                                        onDateChange = { startDate = it }
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    DatePickerField(
                                        label = "Đến ngày",
                                        date = endDate,
                                        onDateChange = { endDate = it }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                                            scope.launch {
                                                viewModel.applyDateFilter(startDate, endDate)
                                            }
                                        }
                                    },
                                    enabled = startDate.isNotEmpty() && endDate.isNotEmpty(),
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                ) {
                                    Text("Áp dụng")
                                }
                            }
                        }
                        "year" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = yearDropdownExpanded,
                                    onExpandedChange = { yearDropdownExpanded = it },
                                    modifier = Modifier.fillMaxWidth(0.7f)
                                ) {
                                    OutlinedTextField(
                                        value = selectedYear,
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Chọn năm") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = yearDropdownExpanded
                                            )
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = yearDropdownExpanded,
                                        onDismissRequest = { yearDropdownExpanded = false }
                                    ) {
                                        years.forEach { year ->
                                            DropdownMenuItem(
                                                text = { Text(year) },
                                                onClick = {
                                                    selectedYear = year
                                                    yearDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            viewModel.applyYearFilter(selectedYear.toInt())
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                ) {
                                    Text("Áp dụng")
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Summary cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoCard(
                        title = "Số đơn hàng",
                        value = orderCount.toString(),
                        icon = Icons.Default.ReceiptLong
                    )

                    InfoCard(
                        title = "Tổng doanh thu",
                        value = formatCurrency(totalRevenue),
                        icon = Icons.Default.AttachMoney
                    )
                }

                // Revenue chart
                if (revenueData.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Biểu đồ doanh thu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            RevenueChart(revenueData, filterMode)
                        }
                    }
                }

                // Orders table
                if (deliveredOrders.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Danh sách đơn hàng",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Table header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "STT",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(0.5f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "Khách hàng",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(2f)
                                )
                                Text(
                                    "Mã đơn hàng",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    "Sản phẩm",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(2f)
                                )
                                Text(
                                    "Tổng tiền",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.5f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    "Ngày",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.5f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Divider()

                            // Table content
                            LazyColumn {
                                itemsIndexed(deliveredOrders) { index, order ->
                                    OrderRow(index + 1, order, formatCurrency(order.totalPrice))

                                    // Add divider between items
                                    if (index < deliveredOrders.size - 1) {
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // No data message
                    if (!isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Không có dữ liệu đơn hàng cho khoảng thời gian đã chọn",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (filterMode == "date_range") {
                                            scope.launch {
                                                // Reset to current month
                                                startDate = monthStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                endDate = monthEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                viewModel.applyDateFilter(startDate, endDate)
                                            }
                                        } else {
                                            scope.launch {
                                                // Reset to current year
                                                selectedYear = LocalDate.now().year.toString()
                                                viewModel.applyYearFilter(selectedYear.toInt())
                                            }
                                        }
                                    }
                                ) {
                                    Text("Quay lại khoảng thời gian hiện tại")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun formatCurrency(amount: Double): String {
        return String.format("%,.0f đ", amount)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerField(label: String, date: String, onDateChange: (String) -> Unit) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()

        // Parse existing date if available
        if (date.isNotEmpty()) {
            try {
                val parts = date.split("/")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Calendar months are 0-based
                    val year = parts[2].toInt()
                    calendar.set(year, month, day)
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "%02d/%02d/%d".format(selectedDay, selectedMonth + 1, selectedYear)
                onDateChange(selectedDate)
            },
            year, month, day
        )

        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Select date",
                    modifier = Modifier.clickable { datePickerDialog.show() }
                )
            },
            modifier = Modifier
                .width(150.dp)
                .clickable { datePickerDialog.show() }
        )
    }

    @Composable
    fun InfoCard(title: String, value: String, icon: ImageVector) {
        Card(
            modifier = Modifier
                .width(180.dp)
                .height(120.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    @Composable
    fun OrderRow(index: Int, order: OrderDataModels, formattedPrice: String) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            color = if (index % 2 == 0) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    index.toString(),
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    "${order.firstName} ${order.lastName}",
                    modifier = Modifier.weight(2f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    order.orderId.take(8) + if (order.orderId.length > 8) "..." else "",
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    order.name.take(15) + if (order.name.length > 15) "..." else "",
                    modifier = Modifier.weight(2f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    formattedPrice,
                    modifier = Modifier.weight(1.5f),
                    textAlign = TextAlign.End
                )
                Text(
                    order.date,
                    modifier = Modifier.weight(1.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun RevenueChart(data: Map<String, Double>, filterMode: String) {
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    description.isEnabled = false
                    setDrawGridBackground(false)
                    legend.isEnabled = true
                    setDrawBarShadow(false)
                    setDrawValueAboveBar(true)

                    // Customize X axis
                    xAxis.apply {
                        setDrawGridLines(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        setCenterAxisLabels(false)
                        labelRotationAngle = 45f  // Rotate labels for better visibility
                        textSize = 10f
                    }

                    // Customize Y axis
                    axisLeft.apply {
                        setDrawGridLines(true)
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return if (value >= 1_000_000) {
                                    String.format("%.1fM", value / 1_000_000)
                                } else if (value >= 1_000) {
                                    String.format("%.1fK", value / 1_000)
                                } else {
                                    value.toInt().toString()
                                }
                            }
                        }
                    }
                    axisRight.isEnabled = false

                    // Additional customization
                    animateY(1000)
                    setPinchZoom(true)  // Enable pinch zoom
                    setScaleEnabled(true)
                    extraBottomOffset = 10f // Add space for rotated labels
                }
            },
            update = { chart ->
                // Sort data entries chronologically
                val sortedData = when (filterMode) {
                    "year" -> {
                        // For year mode, sort by month number
                        val monthMap = mapOf(
                            "Tháng 1" to 1, "Tháng 2" to 2, "Tháng 3" to 3,
                            "Tháng 4" to 4, "Tháng 5" to 5, "Tháng 6" to 6,
                            "Tháng 7" to 7, "Tháng 8" to 8, "Tháng 9" to 9,
                            "Tháng 10" to 10, "Tháng 11" to 11, "Tháng 12" to 12
                        )
                        data.toList().sortedBy { (key, _) ->
                            monthMap[key.split("/")[0]] ?: 0
                        }.toMap()
                    }
                    else -> {
                        // For date range mode, sort by date
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        data.toList().sortedBy { (key, _) ->
                            try {
                                LocalDate.parse(key, formatter)
                            } catch (e: Exception) {
                                LocalDate.now()
                            }
                        }.toMap()
                    }
                }

                val entries = sortedData.entries.mapIndexed { index, (_, value) ->
                    BarEntry(index.toFloat(), value.toFloat())
                }

                val labels = sortedData.keys.toList()

                val dataSet = BarDataSet(entries, "Doanh thu").apply {
                    color = Color.parseColor("#4285F4")
                    valueTextColor = Color.BLACK
                    valueTextSize = 10f
                    setDrawValues(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return if (value >= 1_000_000) {
                                String.format("%.1fM", value / 1_000_000)
                            } else if (value >= 1_000) {
                                String.format("%.1fK", value / 1_000)
                            } else {
                                value.toInt().toString()
                            }
                        }
                    }
                }

                chart.data = BarData(dataSet).apply {
                    barWidth = 0.6f
                }

                // Set labels on X-axis
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                // Set visible X items based on data size
                if (sortedData.size > 10) {
                    chart.setVisibleXRangeMaximum(8f)
                    chart.moveViewToX(0f)
                }

                chart.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }