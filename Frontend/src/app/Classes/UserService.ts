import { Injectable } from '@angular/core';
import { HttpClient ,HttpHeaders,HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './User';
import { LoginResponse } from './LoginResponse';

@Injectable({
  providedIn: 'root'
})
export class UserService {


  private apiUrl = 'http://localhost:8087/Stage/login';

  constructor(private http: HttpClient) { }

  addUser(user: User): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json' // Set the content type to JSON
      })
    };

    return this.http.post(`${this.apiUrl}/signup`, user, httpOptions); // Send the user object directly
  }

  deleteUser(userId: string): Observable<any> {
    const url = `${this.apiUrl}/delete-user/${userId}`;
    return this.http.delete(url);
  }


  logout(token: string): Observable<string> {
    // Create an HttpHeaders object with the Authorization header
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    // Make a POST request to the /logout endpoint with the headers
    const url = `${this.apiUrl}/logout`;

    // Specify responseType as 'text' to treat the response as plain text
    const options = { headers, responseType: 'text' as 'json' };

    return this.http.post<string>(url, {}, options);
  }


  getUserByid (userId: number): Observable<User> {
    const url = `${this.apiUrl}/getUserById/${userId}`;
    return this.http.get<User>(url);
  }


  getAllUsers(): Observable<User[]> {
    const url = `${this.apiUrl}/all`;
    return this.http.get<User[]>(url);
  }

  addPointsToUser(username: string, pointsToAdd: number): Observable<any> {
    const url = `${this.apiUrl}/${username}/add-points`;

    // Create a HttpParams object to set the 'pointsToAdd' parameter
    const params = new HttpParams().set('pointsToAdd', pointsToAdd.toString());

    // Use params as options to include the 'pointsToAdd' parameter in the request
    return this.http.post(url, null, { params });
  }

  login(username: string, password: string): Observable<LoginResponse> { // Update the return type
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    const loginRequest = { username, password };
    return this.http.post<LoginResponse>(`${this.apiUrl}/normalLogin`, loginRequest, httpOptions); // Specify the response type
  }

  initiateFacebookLogin(): Observable<string> {
    return this.http.get(`${this.apiUrl}/facebook/Authorization`, { responseType: 'text' });
  }



  initiateGoogleLogin(): Observable<string> {
    return this.http.get(`${this.apiUrl}/google/login`, { responseType: 'text' });
  }

  initiateLinkedInLogin(): Observable<string> {
    return this.http.get(`${this.apiUrl}/linkedin/login`, { responseType: 'text' });
  }






}
