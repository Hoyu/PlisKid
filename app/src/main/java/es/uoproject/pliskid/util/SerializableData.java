package es.uoproject.pliskid.util;

import es.uoproject.pliskid.modelo.Pack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darkm_000 on 14/05/2015.
 */
public class SerializableData implements Serializable {
    private static final long serialVersionUID = 0L;

    public List<Pack> packs= new ArrayList<Pack>();
}
