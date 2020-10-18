package neat;

import java.io.Serializable;

public interface CreateStrategy extends Serializable {
    Node create(NodePurpose purpose, int innovationNumber);
}
