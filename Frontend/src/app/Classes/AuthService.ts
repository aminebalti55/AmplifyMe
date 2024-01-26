import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt'
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private userToken: string | null = null;
  private jwtHelper = new JwtHelperService();
  private username: string | null = null;


  setToken(token: string): void {
    this.userToken = token;
  }

  getToken(): string | null {
    return this.userToken;
  }

  setUsername(username: string): void {
    this.username = username;
    // Store the username in localStorage
    localStorage.setItem('username', username);
  }

  getUsername(): string | null {
    // Retrieve the username from memory or localStorage
    return this.username || localStorage.getItem('username') || null;
  }

  setTokenx(token: string): void {
    localStorage.setItem('userToken', token);
  }

  getUserId(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decodedToken = this.jwtHelper.decodeToken(token);
        return decodedToken.userId || null; // Replace 'userId' with the actual key in your token
      } catch (error) {
        console.error('Error decoding token:', error);
      }
    }
    return null;
  }

  clearToken(): void {
    // Clear the token from the local storage
    localStorage.removeItem('userToken');

    // Set the userToken variable to null
    this.userToken = null;
  }

 getTokenx(): string | null {
  // Retrieve the user's token from localStorage
  const token = localStorage.getItem('userToken');
  return token;
}

  isAuthenticated(): boolean {
    const token = this.getTokenx(); // Implement a method to get the user's token
    return !!token; // Return true if a token exists, indicating authentication
  }

}
