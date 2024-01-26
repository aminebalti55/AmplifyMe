export class User {
  Id: any;
  username: any;
  fullname: any;
  email: any;
  emailVerified: boolean = false; // Assign a default value
  password: any;
  provider: any; // You might want to use an enum if needed
  providerId: any;
  points: any;
  facebookPage: any; // You can define a similar TypeScript model for FacebookPage
  fullnameError: string = "";
  usernameError: string ="";
  emailError: string =" ";
}
