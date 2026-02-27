import { Pipe, PipeTransform } from "@angular/core";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";

@Pipe({
  name: "safe",
})
export class SafePipe implements PipeTransform {
  constructor(protected sanitizer: DomSanitizer) {}

  transform(value: string, type: string): SafeResourceUrl {
    if (!value) return "";

    if (type === "url") {
      let url = value;

      // --- YOUTUBE TRANSFORMATION ---
      if (url.includes("youtube.com/watch?v=")) {
        const id = url.split("v=")[1].split("&")[0];
        url = `https://www.youtube.com/embed/${id}`;
      } else if (url.includes("youtu.be/")) {
        const id = url.split("youtu.be/")[1].split("?")[0];
        url = `https://www.youtube.com/embed/${id}`;
      }

      // --- VIMEO TRANSFORMATION ---
      // Convert vimeo.com/12345 -> player.vimeo.com/video/12345
      else if (url.includes("vimeo.com/")) {
        const id = url.split("vimeo.com/")[1].split("?")[0];
        url = `https://player.vimeo.com/video/${id}`;
      }

      return this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
    return this.sanitizer.bypassSecurityTrustResourceUrl(value);
  }
}
