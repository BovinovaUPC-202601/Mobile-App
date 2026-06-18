package pe.edu.upc.vacapp.shared.session

/**
 * Registry of per-feature state resetters.
 *
 * The feature DI modules hold their ViewModels as process-wide singletons, so
 * without an explicit reset their in-memory state (cached bovines, a sticky
 * "requires Plus" flag, chat history, etc.) survives a logout and leaks into the
 * next user's session. Each module registers a reset callback the first time it
 * is used; [reset] runs them all on logout so the next user starts clean.
 */
object SessionScope {
    private val resetters = mutableListOf<() -> Unit>()

    @Synchronized
    fun register(resetter: () -> Unit) {
        resetters += resetter
    }

    @Synchronized
    fun reset() {
        resetters.forEach { it() }
    }
}
