package de.elbe5.response;

import de.elbe5.request.RequestData;

public class ModalPage extends HtmlResponse {

    public ModalPage createHtml(RequestData rdata) {
        appendHtml(rdata);
        return this;
    }

    protected void appendHtml(RequestData rdata) {

    }

    public void appendModalStart(String title) {
        append("""
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
                title);
    }

    public void appendModalBodyStart(String title) {
        append("""
                                <div class="modal-body">
                                        <h3>{1}
                                        </h3>
                        """,
                title
        );
    }

    public void appendModalBodyStart() {
        append("""
                                <div class="modal-body">
                        """
                );
    }

    public void appendModalFooter(String secondary, String primary) {
        append("""                
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
                primary);
    }

    public void appendModalFooter(String secondary) {
        append("""                
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">{1}
                                    </button>
                                </div>
                            </div>
                        </div>
                        """,
                secondary);
    }

    public void appendModalEnd() {
        append("""                
                    </div>
                </div>
                """);
    }

}
