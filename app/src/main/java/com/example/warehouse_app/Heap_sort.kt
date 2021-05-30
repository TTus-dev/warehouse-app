package com.example.warehouse_app

import androidx.appcompat.app.AppCompatActivity

open class Heap_sort : AppCompatActivity() {

    var heapSize = 0
    
    var items_bool = true

    fun compare_str(str1: ArrayList<String>, str2: ArrayList<String>, id: Int) : Boolean{
        val bigger: Boolean
        if ((id > 0 && items_bool) || id > 1) {
            bigger = str1[id].toInt() > str2[id].toInt()
        }
        else{
            bigger = str1[id] > str2[id]
        }
        return bigger
    }

    fun left(i: Int): Int {
        return 2 * i
    }

    fun right(i: Int): Int {
        return 2 * i + 1
    }

    fun swap(A: ArrayList<ArrayList<String>>, i: Int, j: Int) {
        val temp = A[i]
        A[i] = A[j]
        A[j] = temp
    }

    fun max_heapify(A: ArrayList<ArrayList<String>>, i: Int, id: Int) {
        val l = left(i)
        val r = right(i)
        var largest: Int

        if ((l <= heapSize - 1) && compare_str(A[l], A[i], id)) {
            largest = l
        } else
            largest = i

        if ((r <= heapSize - 1) && compare_str(A[r], A[l], id)) {
            largest = r
        }

        if (largest != i) {
            swap(A, i, largest)
            max_heapify(A, largest, id)
        }
    }

    fun buildMaxheap(A: ArrayList<ArrayList<String>>, id: Int) {
        heapSize = A.size
        for (i in heapSize / 2 downTo 0) {
            max_heapify(A, i, id)
        }
    }

    fun heap_sort(A: ArrayList<ArrayList<String>>, id: Int) {
        buildMaxheap(A, id)
        for (i in A.size - 1 downTo 1) {
            swap(A, i, 0)
            heapSize = heapSize - 1
            max_heapify(A, 0, id)

        }
    }
}