/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.partybauxserver.system;

/**
 * Controller used to showcase what happens when an exception is thrown
 *
 * @author Michael Isvy
 * @Modified by Tanmay Ghosh
 * <p/>
 * Also see how a view that resolves to "error" has been added ("error.html").
 */

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CrashController {

    @RequestMapping(method = RequestMethod.GET, path = "/oups")
    public String triggerException() {
        throw new RuntimeException("Expected: controller used to showcase what "
                + "happens when an exception is thrown");
    }

//    @GetMapping("/**")
//    public String triggerExceptionRandom() {
//        throw new RuntimeException("Exception Thrown, check log.");
//    }

}
