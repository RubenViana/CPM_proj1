package org.feup.ticketo.data.storage


object Products {
    val products = listOf(

        Product(1, true, "Coffee", "A cup of freshly brewed coffee", 2.50f),

        Product(2, true, "Soda Drink", "A can of soda drink (cola, lemonade, etc.)", 1.50f),

        Product(3, true, "Popcorn", "A bag of popcorn (salted, buttered, etc.)", 3.00f),

        Product(
            4,
            true,
            "Sandwich",
            "A sandwich (ham and cheese, chicken, etc.)",
            5.00f
        )
    )
}

