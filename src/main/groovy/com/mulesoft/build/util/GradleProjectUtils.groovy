/*
 * Copyright 2015 juancavallotti.
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

package com.mulesoft.build.util

import com.mulesoft.build.muleagent.MuleAgentPluginExtension

/**
 * Created by juancavallotti on 5/7/15.
 */
class GradleProjectUtils {

    /**
     * We consider a legacy version a version previous than the default one. In the future, this sense
     * of legacy version may change.
     * @param The string representation of the mule version.
     * @return true if it is a legacy version of mule
     */
    static boolean isLegacyVersion(String version) {
        return false
    }

    /**
     * Convert a version string to an actual version.
     * @param version
     * @return
     */
    static MuleVersion stringToVersion(String version) {

    }

    /**
     * Compares the left side version with the right side version and returns an integer
     * that repsesents this comparison, greater to 0 if the left version is newer than
     * the right version, 0 if they are the same version, lower than 0 if the right version
     * is newer than the left version.
     * @param left version considered as 'left side'
     * @param right version considered ad 'right side'
     * @return the result of the comparison.
     */
    static int compareVersions(MuleVersion left, MuleVersion right) {

    }

    /**
     * Compares the left side version with the right side version string and returns an integer
     * that repsesents this comparison, greater to 0 if the left version is newer than
     * the right version, 0 if they are the same version, lower than 0 if the right version
     * is newer than the left version.
     * @param left version considered as 'left side'
     * @param right version considered ad 'right side'
     * @return the result of the comparison.
     */
    static int compareVersions(String left, String right) {
        return compareVersions(stringToVersion(left), stringToVersion(right))
    }
}
