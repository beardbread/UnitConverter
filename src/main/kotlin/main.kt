const val const = 1.0
const val distance = "Length"
const val weight = "Weight"
const val temp = "Temp"

enum class Unit (val value: Double, val type: String, val nameOfUnit: String) {
    M(const, distance, "meter"),
    KM(const / 1000, distance, "kilometer"),
    CM(const * 100, distance, "centimeter"),
    MM(const * 1000, distance, "millimeter"),
    MI(const / 1609.35, distance, "mile"),
    YD(const / 0.9144, distance, "yard"),
    FT(const / 0.3048, distance, "foot"),
    IN(const / 0.0254, distance, "inch"),

    G(const, weight, "gram"),
    KG(const / 1000, weight, "kilogram"),
    MG(const * 1000, weight, "milligram"),
    LB(const / 453.592, weight, "pound"),
    OZ(const / 28.3495, weight, "ounce"),

    CEL(const, temp, "degree Celsius"),
    FAR(const * 9 / 5 + 32, temp, "degree Fahrenheit"),
    KEL(const + 273.15, temp, "kelvin"),

    NULL(0.0, "", "null");

    companion object {
        fun getUnit(str: String): Unit {
            var unit: Unit = NULL
            when (str) {
                "m", "meter", "meters" -> unit = M
                "km", "kilometer", "kilometers" -> unit = KM
                "cm", "centimeter", "centimeters" -> unit = CM
                "mm", "millimeter", "millimeters" -> unit = MM
                "mi", "mile", "miles" -> unit = MI
                "yd", "yard", "yards" -> unit = YD
                "ft", "foot", "feet" -> unit = FT
                "in", "inch", "inches" -> unit = IN

                "g", "gram", "grams" -> unit = G
                "kg", "kilogram", "kilograms" -> unit = KG
                "mg", "milligram", "milligrams" -> unit = MG
                "lb", "pound", "pounds" -> unit = LB
                "oz", "ounce", "ounces" -> unit = OZ

                "degree celsius", "degrees celsius", "celsius", "dc", "c" -> unit = CEL
                "degree fahrenheit", "degrees fahrenheit", "fahrenheit", "df", "f" -> unit = FAR
                "kelvin", "kelvins", "k" -> unit = KEL

                else -> unit = NULL
            }
            return unit
        }

        private fun rightPostFix (unit: Unit): String {
            when (unit) {
                FT -> return "feet"
                IN -> return "inches"
                CEL -> return "degrees Celsius"
                FAR -> return "degrees Fahrenheit"
                else -> return "${unit.nameOfUnit}s"
            }
        }

        fun convertUnit(value: Double, originalUnit: Unit, targetUnit: Unit): String {
            var result = 0.0
            if (originalUnit != NULL && targetUnit != NULL) {
                if (originalUnit.type == targetUnit.type) {
                    if (originalUnit.type == temp && targetUnit.type == temp) {
                        when (originalUnit) {
                            CEL -> when (targetUnit) {
                                CEL -> result = value
                                KEL -> result = value + 273.15
                                FAR -> result = value * 9 / 5 + 32
                            }
                            KEL -> when (targetUnit) {
                                KEL -> result = value
                                CEL -> result = value - 273.15
                                FAR -> result = value * 9 / 5 - 459.67
                            }
                            FAR -> when (targetUnit) {
                                FAR -> result = value
                                CEL -> result = (value - 32) * 5 / 9
                                KEL -> result = (value + 459.67) * 5 / 9
                            }
                        }
                    } else if (value > 0) {
                        result =  value * targetUnit.value / originalUnit.value
                    } else return "${originalUnit.type} shouldn't be negative"
                } else return "Conversion from ${rightPostFix(originalUnit)} to ${rightPostFix(targetUnit)} is impossible"
            } else {
                when (originalUnit) {
                    NULL -> when (targetUnit) {
                        NULL -> return "Conversion from ??? to ??? is impossible"
                        else -> return "Conversion from ??? to ${rightPostFix(targetUnit)} is impossible"
                    }
                    else -> when (targetUnit) {
                        NULL -> return "Conversion from ${rightPostFix(originalUnit)} to ??? is impossible"
                    }
                }
            }
            when (value) {
                1.0 -> when (result) {
                    1.0 -> return "$value ${originalUnit.nameOfUnit} is $result ${targetUnit.nameOfUnit}"
                    else -> return "$value ${originalUnit.nameOfUnit} is $result ${rightPostFix(targetUnit)}"
                }
                else -> when (result) {
                    1.0 -> return "$value ${rightPostFix(originalUnit)} is $result ${targetUnit.nameOfUnit}"
                    else -> return "$value ${rightPostFix(originalUnit)} is $result ${rightPostFix(targetUnit)}"
                }
            }
        }
    }
}

fun main() {
    var str = ""
    var value = 0.0
    var originalUnit = ""
    var targetUnit = ""

    while (str != "exit") {
        print("Enter what you want to convert (or exit): ")
        str = readLine()!!.toString()
        if (str == "exit") return
        val arr = str.split(" ")
        try {
            value = arr[0].toDouble()

            if (arr[1].toLowerCase() == "degree" || arr[1].toLowerCase() == "degrees") {
                originalUnit = "${arr[1].toLowerCase()} ${arr[2].toLowerCase()}"
                when (arr[4].toLowerCase()) {
                    "degree", "degrees" -> targetUnit = "${arr[4].toLowerCase()} ${arr[5].toLowerCase()}"
                    else -> targetUnit = arr[4].toLowerCase()
                }
            } else {
                originalUnit = arr[1].toLowerCase()
                targetUnit = when (arr[3].toLowerCase()) {
                    "degree", "degrees" -> "${arr[3].toLowerCase()} ${arr[4].toLowerCase()}"
                    else -> arr[3].toLowerCase()
                }
            }

            val original: Unit = Unit.getUnit(originalUnit)
            val target: Unit = Unit.getUnit(targetUnit)

            println(Unit.convertUnit(value, original, target))
        } catch (e: Exception) {
            println("Parse error")
        }
    }
}