package com.balaabirami.abacusandroid.model


class StockAdjustment {

    var id: String? = null
        set(value) {
            field = value
        }

    var date: String? = null
        set(value) {
            field = value
        }
    var franchise: User? = null
        set(value) {
            field = value
        }
    var student: Student? = null
        set(value) {
            field = value
        }
    var items: List<ItemDetail>? = null
        set(value) {
            field = value
        }
    var orderType: AdjustType? = null
        set(value) {
            field = value
        }
    var damageDate: String? = null
        set(value) {
            field = value
        }
    var requestedBy: String? = null
        set(value) {
            field = value
        }
    var description: String? = null
        set(value) {
            field = value
        }
    var paymentType: PaymentType? = null
        set(value) {
            field = value
        }
    var amount: Int = 0
        set(value) {
            field = value
        }
    var amountReceivedDate: String? = null
        set(value) {
            field = value
        }

    enum class AdjustType {
        DAMAGE,
        REISSUE
    }

    enum class PaymentType {
        FREE,
        CASH
    }

    class ItemDetail() {
        var name: Stock? = null
            get() = field
            set(value) {
                field = value
            }
        var qty: Int = 0
            get() = field
            set(value) {
                field = value
            }

        fun toDisplay(): String {
            return "${name?.name} : $qty"
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val itemDetail = o as ItemDetail
            return name?.name.equals(itemDetail.name?.name)
        }
    }

    fun isValid(): String {
        if (date.isNullOrEmpty()) {
            return "Please enter order date"
        }
        if (franchise == null) {
            return "Please enter Franchise details"
        }
        if (student == null) {
            return "Please enter Student details"
        }
        if (items.isNullOrEmpty()) {
            return "Please add atleast one item"
        }
        if (orderType == null) {
            return "Please select Order type"
        }
        if (orderType == AdjustType.DAMAGE) {
            if (damageDate.isNullOrEmpty()) {
                return "Please enter damage date"
            }
        } else if (orderType == AdjustType.REISSUE) {
            if (requestedBy.isNullOrEmpty()) {
                return "Please enter requestee"
            }
        }
        if (description.isNullOrEmpty()) {
            return "Please enter description"
        }
        if (paymentType == null) {
            return "Please select payment type"
        }
        if (paymentType == PaymentType.CASH) {
            if (amount <= 0) {
                return "Please enter amount"
            }
            if (amountReceivedDate.isNullOrEmpty()) {
                return "Please enter amount received date"
            }
        }
        return ""
    }

    companion object {
        @JvmStatic
        fun createId(): String {
            return "SA" + System.currentTimeMillis().toString()
        }
    }


}