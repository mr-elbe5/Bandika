package de.elbe5.response;

import de.elbe5.request.RequestData;

import java.util.Map;

public class ModalPage extends HtmlResponse implements IHtmlBuilder{

    public ModalPage createHtml(RequestData rdata) {
        appendHtml(rdata);
        return this;
    }

    protected void appendHtml(RequestData rdata) {

    }

    public void appendModalStart(String title) {
        append(sb, """
                        <div class="modal-dialog modal-lg" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                  <h5 class="modal-title">$title$
                                  </h5>
                                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                      <span aria-hidden="true">&times;</span>
                                  </button>
                                </div>
                        """,
                Map.ofEntries(
                        param("title",title)
                )
        );
    }

    public void appendModalBodyStart(String title) {
        append(sb, """
                                <div class="modal-body">
                                        <h3>$title$</h3>
                        """,
                Map.ofEntries(
                        param("title",title)
                )
        );
    }

    public void appendModalBodyStart() {
        append(sb, """
                                <div class="modal-body">
                        """
                );
    }

    public void appendModalFooter(String secondary, String primary) {
        append(sb, """                
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">$secondary$
                                    </button>
                                    <button type="submit" class="btn btn-primary">$primary$
                                    </button>
                                </div>
                            </div>
                        </div>
                        """,
                Map.ofEntries(
                        param("secondary",secondary),
                        param("primary",primary)
                )
        );
    }

    public void appendModalFooter(String secondary) {
        append(sb, """                
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">$secondary$
                                    </button>
                                </div>
                            </div>
                        </div>
                        """,
                Map.ofEntries(
                        param("secondary",secondary)
                )
        );
    }

    public void appendModalEnd() {
        append(sb, """                
                    </div>
                </div>
                """);
    }

}
