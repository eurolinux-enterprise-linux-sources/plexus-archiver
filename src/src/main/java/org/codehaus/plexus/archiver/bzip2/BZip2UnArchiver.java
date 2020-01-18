package org.codehaus.plexus.archiver.bzip2;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Revision$ $Date$
 */
public class BZip2UnArchiver
    extends AbstractUnArchiver
{
    public BZip2UnArchiver()
    {
    }

    public BZip2UnArchiver( File sourceFile )
    {
        super( sourceFile );
    }

    protected void execute()
        throws ArchiverException
    {
        if ( getSourceFile().lastModified() > getDestFile().lastModified() )
        {
            getLogger().info( "Expanding " + getSourceFile().getAbsolutePath() + " to "
                              + getDestFile().getAbsolutePath() );

            FileOutputStream out = null;
            BZip2CompressorInputStream zIn = null;
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try
            {
                out = new FileOutputStream( getDestFile() );
                fis = new FileInputStream( getSourceFile() );
                bis = new BufferedInputStream( fis );
                zIn = getBZip2InputStream( bis );
                if ( zIn == null )
                {
                    throw new ArchiverException( getSourceFile().getAbsolutePath() + " is an invalid bz2 file." );
                }
                byte[] buffer = new byte[8 * 1024];
                int count = 0;
                do
                {
                    out.write( buffer, 0, count );
                    count = zIn.read( buffer, 0, buffer.length );
                }
                while ( count != -1 );
            }
            catch ( IOException ioe )
            {
                String msg = "Problem expanding bzip2 " + ioe.getMessage();
                throw new ArchiverException( msg, ioe );
            }
            finally
            {
                IOUtil.close( bis );
                IOUtil.close( fis );
                IOUtil.close( out );
                IOUtil.close( zIn );
            }
        }
    }

    public static BZip2CompressorInputStream getBZip2InputStream( InputStream bis )
        throws IOException
    {
        return new BZip2CompressorInputStream( bis );
    }

    protected void execute( String path, File outputDirectory )
    {
        throw new UnsupportedOperationException( "Targeted extraction not supported in BZIP2 format." );
    }
}
