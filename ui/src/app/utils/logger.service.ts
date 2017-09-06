import { Injectable } from '@angular/core';


@Injectable()
export class LoggerService {

    debug(text: string): void {
        console.log('[DEBUG] ' + text);
    }

    error(err: string): void {
        console.log('[ERROR] ' + err);
    }

}
