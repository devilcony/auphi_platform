/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0 

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.auphi.ktrl.i18n;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class LanguageChoice
{
    private static final String STRING_FAILOVER_LOCALE        = "LocaleFailover";
    private static final String STRING_DEFAULT_LOCALE         = "LocaleDefault";
    
    
    
    private static LanguageChoice choice;
    
    private Locale defaultLocale;
    private Locale failoverLocale;
    
    
    private LanguageChoice()
    {
        try
        {
            loadSettings();
        }
        catch(IOException e)  // Can't load settings: set the default
        {
            defaultLocale = Locale.getDefault();
            failoverLocale = Locale.US;
        }
    }
    
    public static final LanguageChoice getInstance()
    {
        if (choice!=null) return choice;
        
        choice = new LanguageChoice();
        
        // System.out.println("Loaded language choices: default="+choice.defaultLocale.toString()+", failover="+choice.failoverLocale.toString());
        
        return choice;
    }

    /**
     * @return Returns the defaultLocale.
     */
    public Locale getDefaultLocale()
    {
        return defaultLocale;
    }

    /**
     * @param defaultLocale The defaultLocale to set.
     */
    public void setDefaultLocale(Locale defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    /**
     * @return Returns the failoverLocale.
     */
    public Locale getFailoverLocale()
    {
        return failoverLocale;
    }

    /**
     * @param failoverLocale The failoverLocale to set.
     */
    public void setFailoverLocale(Locale failoverLocale)
    {
        this.failoverLocale = failoverLocale;
    }
    
    private void loadSettings() throws IOException
    {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/constants_auphi.properties"));
        defaultLocale  = new Locale( properties.getProperty(STRING_DEFAULT_LOCALE,  Locale.getDefault().toString()) );
        failoverLocale = new Locale( properties.getProperty(STRING_FAILOVER_LOCALE, "en_US") );
    }
    
    public void saveSettings()
    {
        try
        {
            Properties properties = new Properties();
            properties.setProperty(STRING_DEFAULT_LOCALE, defaultLocale.toString());
            properties.setProperty(STRING_FAILOVER_LOCALE, failoverLocale.toString());
            properties.store(new FileOutputStream(getSettingsFilename()), "Language Choice");
        }
        catch(IOException e)
        {
            ;
        }
    }
    
    public String getSettingsFilename()
    {
        return getClass().getResource("/").getPath().replaceAll("%20", " ") + "constants_auphi.properties";
    }
}
