import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddSocialPageDialogComponent } from './add-social-page-dialog.component';

describe('AddSocialPageDialogComponent', () => {
  let component: AddSocialPageDialogComponent;
  let fixture: ComponentFixture<AddSocialPageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddSocialPageDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddSocialPageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
