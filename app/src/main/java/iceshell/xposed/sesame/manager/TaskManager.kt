package iceshell.xposed.sesame.manager

/**
 * 任务管理器
 */
class TaskManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: TaskManager? = null
        
        fun getInstance(): TaskManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskManager().also { INSTANCE = it }
            }
        }
    }
}
