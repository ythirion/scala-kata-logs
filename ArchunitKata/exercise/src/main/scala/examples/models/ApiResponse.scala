package examples.models

class ApiResponse[TData](val data: TData,
                         val errors: List[ApiError] = List.empty)