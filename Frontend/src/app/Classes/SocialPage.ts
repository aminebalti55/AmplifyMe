import { SocialMediaType } from "./SocialMediaType";
import { SocialOperation } from "./SocialOperation";
import { User } from "./User";
export class SocialPage {
  id?: number; // Mark the 'id' property as optional with the '?'
  UserId!: any;
  pageLink!: string;
  socialMediaType!: SocialMediaType;
  socialOperation!: SocialOperation;
  pointsToSpend!: number;
}
