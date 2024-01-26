export interface LoginResponse {
  authenticated: boolean;
  user: any; // Define the user type according to your backend
  role: string; // Add the role field
  message?: string; // Optional error message
  token: string; // Add the token field

}
