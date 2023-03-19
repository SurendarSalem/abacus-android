package com.balaabirami.abacusandroid.model

class Session {

    companion object {
        private var steps = mutableListOf<String>()

        fun addStep(step: String) {
            steps.add(step)
        }

        fun clear() {
            steps.clear()
        }

        fun getSteps(): MutableList<String> {
            return steps
        }
    }
}