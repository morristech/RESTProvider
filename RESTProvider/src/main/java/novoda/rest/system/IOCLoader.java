
package novoda.rest.system;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class IOCLoader {

    private Context context;

    private PackageManager pm;

    public IOCLoader(final Context context) {
        this.context = context;
        this.pm = context.getPackageManager();
    }

    public ServiceInfo getServiceInfo(String service) {
        try {
            ComponentName component = new ComponentName(context, getService(service).getClass());
            return pm.getServiceInfo(component, PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            throw new LoaderException(e);
        }
    }

    public Service getService(final String name) {
        try {
            return (Service) Class.forName(appendPackageToName(name)).newInstance();
        } catch (Exception e) {
            throw new LoaderException(e);
        }
    }

    private String appendPackageToName(String name) {
        if (name.toCharArray()[0] == '.') {
            return new StringBuilder(context.getPackageName()).append(name).toString();
        }
        return name;
    }

    public class LoaderException extends RuntimeException {

        public static final int INSTANTIATION_EXCEPTION = 0;

        public static final int ILLEGAL_ACCESS_EXCEPTION = 1;

        public static final int CLASS_NOT_FOUND_EXCEPTION = 2;

        public static final int NAME_NOT_FOUND_EXCEPTION = 3;

        private static final long serialVersionUID = 1092262456874490603L;

        private int type;

        public LoaderException(Exception e) {
            super(e);
            if (e instanceof InstantiationException) {
                this.setType(INSTANTIATION_EXCEPTION);
            } else if (e instanceof IllegalAccessException) {
                this.setType(ILLEGAL_ACCESS_EXCEPTION);
            } else if (e instanceof ClassNotFoundException) {
                this.setType(CLASS_NOT_FOUND_EXCEPTION);
            } else if (e instanceof NameNotFoundException) {
                this.setType(NAME_NOT_FOUND_EXCEPTION);
            }
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

}
