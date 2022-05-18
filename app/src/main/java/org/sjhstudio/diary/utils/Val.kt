package org.sjhstudio.diary.utils

class Val {

    companion object {
        /**
         * 일반상수
         */
        const val SELECTED_TAB_INDEX = "selected_tab_index";  // 선택된 프래그먼트(탭) 번호

        /**
         * Request
         */
        const val REQUEST_ALL_PERMISSIONS = 11
        const val REQUEST_WEATHER_BY_GRID = 1
//        const val REQUEST_DETAIL_ACTIVITY = 2

        /**
         * Volley Response
         */
        const val VOLLEY_RESPONSE_OK = 200
        const val VOLLEY_RESPONSE_ERROR = 400

        /**
         * Activity Response
         */
        const val DETAIL_ACTIVITY_RESULT_DELETE = -10
        const val DETAIL_ACTIVITY_RESULT_UPDATE = -11
    }
}