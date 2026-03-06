package cm.horion.homegaz.domain.usecase


class RequestLocationPermissionUseCase {

    sealed class Result {
        object Granted : Result()
        object Denied  : Result()
    }

    operator fun invoke(isGranted: Boolean): Result =
        if (isGranted) Result.Granted else Result.Denied
}