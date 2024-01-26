import { Component, OnInit } from '@angular/core';
import { User } from '../Classes/User';
import { ActivatedRoute ,Router } from '@angular/router';

import { UserService } from '../Classes/UserService';
@Component({
  selector: 'app-Callback',
  templateUrl: './Callback.component.html',
  styleUrls: ['./Callback.component.css']
})
export class CallbackComponent implements OnInit {

  constructor(private route: ActivatedRoute,
    private userService: UserService,private router: Router) { }


    ngOnInit() {

  }

}
