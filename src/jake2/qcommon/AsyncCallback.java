package jake2.qcommon;

/**
 * TODO(jgw): Use the GWT one when we switch over.
 */
public interface AsyncCallback<T> {

  void onSuccess(T response);
  void onFailure(Throwable e);
}
