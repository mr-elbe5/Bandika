package de.elbe5.html;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.response.HtmlResponse;

public class ModalPage extends HtmlResponse {

    public ModalPage createHtml(RequestData rdata) {
        StringBuilder sb = new StringBuilder();
        appendHtml(sb, rdata);
        html = sb.toString();
        return this;
    }

    protected void appendHtml(StringBuilder sb, RequestData rdata) {

    }

    public void appendModalStart(StringBuilder sb, String title) {
        sb.append(Strings.format("""
                        <div class="modal-dialog modal-lg" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                  <h5 class="modal-title">{1}
                                  </h5>
                                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                      <span aria-hidden="true">&times;</span>
                                  </button>
                                </div>
                        """,
                title));
    }

    public void appendModalBodyStart(StringBuilder sb, RequestData rdata, String title){
        sb.append(Strings.format("""
                        <div class="modal-body">
                                <h3>{1}
                                </h3>
                """,
                title
                ));
    }

    public void appendModalEnd(StringBuilder sb, String secondary, String primary) {
        sb.append(Strings.format("""                
                                </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">{1}
                                        </button>
                                        <button type="submit" class="btn btn-primary">{2}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        """,
                secondary,
                primary));
    }

}
