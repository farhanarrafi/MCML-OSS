package jp.go.nict.commons.sound;

import java.nio.ByteBuffer;

/**
 * STMD G711 Codec.
 * Title: STMD G711 Codec.
 * @author Kimura Noriyuki
 * @version 2.00
 * @since 2011/06/10
 */
abstract public class G711Codec extends Object
{
    /* Kinds of Law on Codec Algorism */
    /** u-law algorism. */
    public static final int U_LAW   = 0;
    /** A-law algorism. */
    public static final int A_LAW   = 1;

    /**
     * Default Constructor.
     */
    G711Codec()
    {
        super();
    }

    /**
     * Instance Factory method.
     */
    public static G711Codec getInstance(int law)
    {
        G711Codec instance = null;

        /* u-law */
        if(law == U_LAW)
        {
            instance = (G711Codec)new G711ULawCodec();
        }
        /* Specified law is not supported. */
        else
        {
            return null;
        }

        return (G711Codec)instance;
    }

    /**
     * Compress method for Override.
     */
    public ByteBuffer compress(ByteBuffer targetByteBuffer)
    {
        return null;
    }

    /**
     * Expand method for Override.
     */
    public ByteBuffer expand(ByteBuffer targetByteBuffer)
    {
        return null;
    }
}

