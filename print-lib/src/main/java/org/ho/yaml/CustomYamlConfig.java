package org.ho.yaml;

import org.ho.util.BiDirectionalMap;
import org.ho.yaml.wrapper.ArrayWrapper;
import org.ho.yaml.wrapper.DefaultCollectionWrapper;
import org.ho.yaml.wrapper.DefaultMapWrapper;
import org.ho.yaml.wrapper.DefaultSimpleTypeWrapper;
import org.ho.yaml.wrapper.EnumWrapper;
import org.ho.yaml.wrapper.ObjectWrapper;
import org.mapfish.print.config.AddressHostMatcher;
import org.mapfish.print.config.DnsHostMatcher;
import org.mapfish.print.config.LocalHostMatcher;
import org.mapfish.print.config.layout.AttributesBlock;
import org.mapfish.print.config.layout.ColumnDefs;
import org.mapfish.print.config.layout.ColumnsBlock;
import org.mapfish.print.config.layout.ImageBlock;
import org.mapfish.print.config.layout.Layouts;
import org.mapfish.print.config.layout.MapBlock;
import org.mapfish.print.config.layout.TextBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomYamlConfig extends YamlConfig {
    public CustomYamlConfig() {
        Map<String, Object> handlers = new HashMap<String, Object>();
        handlers.put(Layouts.class.getName(), Layouts.Wrapper.class.getName());
        handlers.put(ColumnDefs.class.getName(), ColumnDefs.Wrapper.class.getName());
        setHandlers(handlers);

        BiDirectionalMap<String, String> transfers = new BiDirectionalMap<String, String>();

        //blocks
        transfers.put("text", TextBlock.class.getName());
        transfers.put("image", ImageBlock.class.getName());
        transfers.put("columns", ColumnsBlock.class.getName());
        transfers.put("map", MapBlock.class.getName());
        transfers.put("attributes", AttributesBlock.class.getName());

        //hosts matchers
        transfers.put("localMatch", LocalHostMatcher.class.getName());
        transfers.put("ipMatch", AddressHostMatcher.class.getName());
        transfers.put("dnsMatch", DnsHostMatcher.class.getName());

        setTransfers(transfers);
    }

    public ObjectWrapper getWrapper(String classname) {
        ObjectWrapper ret;
        Class<?> type = ReflectionUtil.classForName(transfer2classname(classname));
        if (type == null) {
            return null;
        }
        if (handlers != null && handlers.containsKey(classname)) {
            ret = initWrapper(classname, type);
        } else {
            if (Map.class.isAssignableFrom(type)) {
                ret = new DefaultMapWrapper(type);
            } else if (Collection.class.isAssignableFrom(type)) {
                ret = new DefaultCollectionWrapper(type);
            } else if (type.isArray()) {
                ret = new ArrayWrapper(type);
            } else if (ReflectionUtil.isSimpleType(type)) {
                return new DefaultSimpleTypeWrapper(type);
            } else if (type.isEnum()) {
                ret = new EnumWrapper(type);
            } else {
                ret = new CustomBeanWrapper(type);
            }
        }
        ret.setYamlConfig(this);
        return ret;
    }
}
