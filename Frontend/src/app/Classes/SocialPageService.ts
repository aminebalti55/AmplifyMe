import { Injectable } from '@angular/core';
import { HttpClient ,HttpHeaders} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators'; // Import catchError hereimport { SocialMediaType } from "./SocialMediaType";
import { SocialOperation } from "./SocialOperation";
import { User } from "./User";
import { SocialPage } from "./SocialPage";

@Injectable({
  providedIn: 'root',
})
export class SocialPageService {
  private baseUrl = 'http://localhost:8087/Stage/socialPage';

  constructor(private http: HttpClient) {}

   addSocialPage(socialPage: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add`, socialPage);
  }


  getSocialPagesByUserId(userId: number): Observable<SocialPage[]> {
    return this.http.get<SocialPage[]>(`${this.baseUrl}/user/${userId}`);
  }


}
