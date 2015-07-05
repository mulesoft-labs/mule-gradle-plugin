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

import com.mulesoft.build.MulePluginExtension
import com.mulesoft.build.domain.MuleDomainPlugin
import com.mulesoft.build.muleagent.MuleAgentPluginExtension
import org.gradle.api.Project

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
        return compareVersions(version, MulePluginExtension.DEFAULT_MULE_VERSION) < 0
    }

    /**
     * Convert a version string to an actual version.
     * @param version the string representation of the version.
     * @return a complex type defining the version.
     */
    static MuleVersion stringToVersion(String version) {
        if (version == null) {
            return null
        }

        String[] parts = version.split('\\.')

        MuleVersion ret = new MuleVersion()

        if (parts.length != 3) {
            throw new IllegalArgumentException('Version string should be in the format of x.x.x where x is any positive integer.')
        }

        ret.major = parts[0].toInteger()
        ret.minor = parts[1].toInteger()
        ret.patch = parts[2].toInteger()

        return ret
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

        if (left == null || right == null) {
            throw new IllegalArgumentException('Versions must not be null.')
        }

        int partial = left.major - right.major

        if (partial != 0) {
            return  partial
        }

        partial = left.minor - right.minor

        if (partial != 0) {
            return partial
        }

        return left.patch - right.patch
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

    /**
     * Checks the parent project to have applied the mule domain plugin.
     * @param project the project to check.
     * @return true if the parent has the domain plugin applied, false if no parent or not a domain.
     */
    static boolean hasDomainParent(Project project) {
        Project parent = project.getParent()

        if (!parent) {
            return false
        }

        return parent.plugins.hasPlugin(MuleDomainPlugin)
    }
}
